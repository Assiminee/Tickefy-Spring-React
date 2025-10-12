import React, { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { SimpleMatch } from '../types/match';
import { useCart } from '../context/CartContext';

interface SeatSelectionPopupProps {
  matches: SimpleMatch[];
  onClose: () => void;
  onConfirm: (matchSeats: { match: SimpleMatch; seatNumber: number }[]) => Promise<void>;
}

const SeatSelectionPopup: React.FC<SeatSelectionPopupProps> = ({ matches, onClose, onConfirm }) => {
  const { cart } = useCart();
  const [seatSelections, setSeatSelections] = useState<{ [key: number]: { seatNumber: string; isAvailable?: boolean } }>(
    matches.reduce((acc, match) => ({ ...acc, [match.id]: { seatNumber: '', isAvailable: undefined } }), {})
  );
  const [isChecking, setIsChecking] = useState<{ [key: number]: boolean }>({});

  const isMatchAndSeatInCart = (matchToCheck: SimpleMatch, seatNumber: number) => {
    // Check the current cart context including the match date
    return cart.some(item => 
      item.homeTeam.name === matchToCheck.homeTeam.name &&
      item.awayTeam.name === matchToCheck.awayTeam.name &&
      item.seatNumber === seatNumber &&
      new Date(item.date).getTime() === new Date(matchToCheck.date).getTime()
    );
  };

  const checkSeatAvailability = async (match: SimpleMatch, seatNumber: number) => {
    try {
      // First check if the seat is already in the cart for this specific match date
      if (isMatchAndSeatInCart(match, seatNumber)) {
        toast.error(`Seat ${seatNumber} is already in your cart for this match on ${new Date(match.date).toLocaleDateString()}`);
        return false;
      }

      const token = localStorage.getItem('token');
      if (!token) {
        toast.error('Please log in to check seat availability');
        return false;
      }

      // Format the request data as query parameters
      const params = new URLSearchParams({
        matchId: match.id.toString(),
        seatNumber: seatNumber.toString(),
        matchName: `${match.homeTeam.name} VS ${match.awayTeam.name}`,
        matchDate: new Date(match.date).toISOString().slice(0, 16), // Format: yyyy-MM-dd'T'HH:mm
        VenueName: match.venue?.name || 'Unknown Venue',
        VenueCity: match.venue?.city || 'Unknown City'
      });

      // Send request with query parameters
      const response = await fetch(`http://localhost:5001/api/cart/check-seat?${params.toString()}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      // Try to get error details if available
      let errorDetails = '';
      try {
        const errorText = await response.text();
        const errorJson = JSON.parse(errorText);
        errorDetails = errorJson.message ? `: ${errorJson.message}` : (errorText ? `: ${errorText}` : '');
      } catch (e) {
        errorDetails = '';
      }

      if (response.status === 500) {
        toast.error(`Server error: Unable to verify seat availability${errorDetails}`);
        return false;
      }

      if (response.status === 409) {
        toast.error(`Seat ${seatNumber} is not available${errorDetails}`);
        return false;
      }

      if (!response.ok) {
        toast.error(`Failed to verify seat availability${errorDetails}`);
        return false;
      }

      // If we get here, the seat is available
      return true;
    } catch (error) {
      toast.error('Network error: Unable to verify seat availability. Please check your connection.');
      return false;
    }
  };

  const handleSeatChange = async (matchId: number, value: string) => {
    const seatNumber = parseInt(value);
    if (isNaN(seatNumber) || seatNumber < 1) {
      setSeatSelections(prev => ({
        ...prev,
        [matchId]: { seatNumber: value, isAvailable: undefined }
      }));
      return;
    }

    const match = matches.find(m => m.id === matchId);
    if (!match) return;

    // Check if this seat is already in cart
    if (cart.some(item => 
      item.homeTeam.name === match.homeTeam.name &&
      item.awayTeam.name === match.awayTeam.name &&
      item.seatNumber === seatNumber &&
      new Date(item.date).getTime() === new Date(match.date).getTime()
    )) {
      setSeatSelections(prev => ({
        ...prev,
        [matchId]: { seatNumber: value, isAvailable: false }
      }));
      return;
    }

    setIsChecking(prev => ({ ...prev, [matchId]: true }));
    const isAvailable = await checkSeatAvailability(match, seatNumber);
    
    setSeatSelections(prev => ({
      ...prev,
      [matchId]: { seatNumber: value, isAvailable }
    }));
    setIsChecking(prev => ({ ...prev, [matchId]: false }));
  };

  const handleConfirm = () => {
    const invalidSelections = Object.entries(seatSelections).filter(
      ([_, selection]) => !selection.seatNumber || isNaN(parseInt(selection.seatNumber)) || parseInt(selection.seatNumber) < 1
    );

    if (invalidSelections.length > 0) {
      toast.error('Please enter valid seat numbers for all matches');
      return;
    }

    const unavailableSeats = Object.entries(seatSelections).filter(
      ([_, selection]) => selection.isAvailable === false
    );

    if (unavailableSeats.length > 0) {
      toast.error('Some selected seats are not available');
      return;
    }

    const selections = matches.map(match => ({
      match,
      seatNumber: parseInt(seatSelections[match.id].seatNumber)
    }));

    // Check for duplicates in the current cart including match date
    const duplicateInCart = selections.some(({ match, seatNumber }) => 
      cart.some(item => 
        item.homeTeam.name === match.homeTeam.name &&
        item.awayTeam.name === match.awayTeam.name &&
        item.seatNumber === seatNumber &&
        new Date(item.date).getTime() === new Date(match.date).getTime()
      )
    );

    if (duplicateInCart) {
      toast.error('You already have this seat in your cart for this match date');
      return;
    }

    onConfirm(selections);
  };

  const isConfirmDisabled = () => {
    // Check if any selections are invalid or unavailable
    return Object.entries(seatSelections).some(([_, selection]) => 
      !selection.seatNumber || // No seat number entered
      isNaN(parseInt(selection.seatNumber)) || // Invalid number
      parseInt(selection.seatNumber) < 1 || // Number less than 1
      selection.isAvailable === false || // Seat is not available
      selection.isAvailable === undefined // Availability not checked yet
    );
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-gray-900 rounded-xl p-6 max-w-2xl w-full max-h-[80vh] overflow-y-auto">
        <h2 className="text-2xl font-bold text-white mb-4">Select Seats</h2>
        
        <div className="space-y-4">
          {matches.map(match => (
            <div key={match.id} className="bg-gray-800 rounded-lg p-4">
              <div className="flex items-center justify-between mb-3">
                <div className="flex items-center space-x-2">
                  <img src={match.homeTeam.logo} alt={match.homeTeam.name} className="w-8 h-8 object-contain" />
                  <span className="text-white">vs</span>
                  <img src={match.awayTeam.logo} alt={match.awayTeam.name} className="w-8 h-8 object-contain" />
                </div>
                <span className="text-white text-sm">
                  {new Date(match.date).toLocaleDateString()}
                </span>
              </div>
              
              <div className="flex items-center space-x-3">
                <input
                  type="number"
                  min="1"
                  placeholder="Seat number"
                  value={seatSelections[match.id].seatNumber}
                  onChange={(e) => handleSeatChange(match.id, e.target.value)}
                  className={`w-full px-3 py-2 rounded-lg bg-gray-700 text-white border ${
                    seatSelections[match.id].isAvailable === false
                      ? 'border-red-500'
                      : seatSelections[match.id].isAvailable
                      ? 'border-green-500'
                      : 'border-gray-600'
                  }`}
                  disabled={isChecking[match.id]}
                />
                <div className="min-w-[120px] text-sm">
                  {isChecking[match.id] && (
                    <span className="text-yellow-500 flex items-center">
                      <svg className="animate-spin h-4 w-4 mr-2" viewBox="0 0 24 24">
                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                      </svg>
                      Checking...
                    </span>
                  )}
                  {!isChecking[match.id] && seatSelections[match.id].isAvailable === false && (
                    <span className="text-red-500">Not available</span>
                  )}
                  {!isChecking[match.id] && seatSelections[match.id].isAvailable === true && (
                    <span className="text-green-500">Available!</span>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>

        <div className="flex justify-end space-x-3 mt-6">
          <button
            onClick={onClose}
            className="px-4 py-2 bg-gray-700 text-white rounded-lg hover:bg-gray-600 transition"
          >
            Cancel
          </button>
          <button
            onClick={handleConfirm}
            disabled={isConfirmDisabled()}
            className={`px-4 py-2 rounded-lg transition ${
              isConfirmDisabled()
                ? 'bg-gray-600 cursor-not-allowed text-gray-400'
                : 'bg-green-600 hover:bg-green-500 text-white'
            }`}
          >
            Confirm
          </button>
        </div>
      </div>
    </div>
  );
};

export default SeatSelectionPopup; 