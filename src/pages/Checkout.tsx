
import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { toast } from 'react-toastify';
import { useCart } from '../context/CartContext';
import { CartItem } from '../types/match';
import { fetchAndStoreMatchData, getMatchData } from '../utils/matchDataService';

// Simulated AuthComponent
interface AuthComponentProps {
  children: React.ReactNode;
  onAuthSuccess: () => void;
}

const AuthComponent: React.FC<AuthComponentProps> = ({ children, onAuthSuccess }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const { user } = useAuth();

  useEffect(() => {
    if (user) {
      setIsAuthenticated(true);
      onAuthSuccess();
    }
  }, [user, onAuthSuccess]);

  if (!isAuthenticated) {
    return (
      <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 backdrop-blur-sm z-50 min-h-screen">
        <div className="bg-black bg-opacity-80 p-8 rounded-xl shadow-2xl w-full max-w-md mx-4 border border-gray-800 flex flex-col items-center">
          <h2 className="text-2xl font-bold mb-6 text-white text-center">Please Log In</h2>
          <Link
            to="/login"
            className="block w-full max-w-xs py-3 px-4 text-center bg-[#10B981] hover:bg-[#059669] text-white font-semibold rounded-lg transition-all duration-200 transform hover:translate-y-[-2px] hover:shadow-lg"
          >
            Log In
          </Link>
          <p className="mt-4 text-gray-400 text-sm text-center">
            Please log in to complete your purchase
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen w-full flex items-center justify-center">
      {children}
    </div>
  );
};

interface Match {
  id: number;
  homeTeam: {
    name: string;
    logo: string;
  };
  awayTeam: {
    name: string;
    logo: string;
  };
  date: string;
}

interface TicketDetails {
  seatNumber: string;
  VenueName: string;
  VenueCity: string;
  facePhoto: File | null;
}

interface User {
  token: string;
}

interface AuthContextType {
  user: User | null;
}

interface Team {
  name: string;
  logo: string;
}

const Checkout: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { match }: { match?: Match } = location.state || {};
  const { ticketDetails }: { ticketDetails?: TicketDetails } = location.state || {};
  const [isAuthSuccess, setIsAuthSuccess] = useState(false);
  const [cardNumber, setCardNumber] = useState('');
  const [cardholderName, setCardholderName] = useState('');
  const [expirationDate, setExpirationDate] = useState('');
  const [cvv, setCvv] = useState('');
  const [isProcessing, setIsProcessing] = useState(false);
  const [selectedCardType, setSelectedCardType] = useState<'VISA' | 'MASTERCARD' | 'VERVE'>('VISA');
  const { user } = useAuth() as AuthContextType;
  const { clearCart, removeFromCart, addToCart } = useCart();
  const [checkoutItems, setCheckoutItems] = useState<CartItem[]>([]);
  const [totalAmount, setTotalAmount] = useState(0);

  const formatMatchTime = (utcDate: string) => {
    const date = new Date(utcDate);
    return date.toLocaleString([], {
      weekday: 'short',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const defaultHomeTeam = 'BENIN';
  const defaultAwayTeam = 'SENEGAL';
  const defaultDate = 'N/A';
  const defaultLogo = 'https://media.api-sports.io/football/teams/default.png';
  const defaultSeatNumber = 'N/A';

  // Card validation functions
  const formatCardNumber = (value: string) => {
    const v = value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
    const matches = v.match(/\d{4,16}/g);
    const match = matches && matches[0] || '';
    const parts = [];

    for (let i = 0, len = match.length; i < len; i += 4) {
      parts.push(match.substring(i, i + 4));
    }

    if (parts.length) {
      return parts.join(' ');
    } else {
      return value;
    }
  };

  const handleCardNumberChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = formatCardNumber(e.target.value);
    if (value.replace(/\s/g, '').length <= 16) {
      setCardNumber(value);
    }
  };

  const formatExpiryDate = (value: string) => {
    const v = value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
    if (v.length >= 2) {
      const month = parseInt(v.substring(0, 2));
      if (month > 12) {
        return '12/' + v.substring(2, 4);
      }
      return v.substring(0, 2) + (v.length > 2 ? '/' + v.substring(2, 4) : '');
    }
    return v;
  };

  const handleExpiryDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = formatExpiryDate(e.target.value);
    if (value.length <= 5) {
      setExpirationDate(value);
    }
  };

  const handleCvvChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value.replace(/\D/g, '');
    if (value.length <= 3) {
      setCvv(value);
    }
  };

  const validateForm = () => {
    if (!cardNumber || cardNumber.replace(/\s/g, '').length < 16) {
      toast.error('Please enter a valid card number');
      return false;
    }

    if (!cardholderName || cardholderName.trim().length < 3) {
      toast.error('Please enter a valid cardholder name');
      return false;
    }

    if (!expirationDate || !expirationDate.match(/^(0[1-9]|1[0-2])\/([0-9]{2})$/)) {
      toast.error('Please enter a valid expiration date (MM/YY)');
      return false;
    }

    if (!cvv || cvv.length !== 3) {
      toast.error('Please enter a valid CVV');
      return false;
    }

    return true;
  };

  useEffect(() => {
    const loadCheckoutItems = async () => {
      try {
        await fetchAndStoreMatchData();
        const matchData = await getMatchData();

        const storedData = sessionStorage.getItem('checkoutData');
        console.log('Retrieved checkout data:', storedData);
        if (!storedData) {
          toast.error('No items to checkout');
          navigate('/cart');
          return;
        }

        let data;
        try {
          data = JSON.parse(storedData);
          if (!data.items || !Array.isArray(data.items)) {
            throw new Error('Invalid checkout data structure');
          }
        } catch (parseError) {
          throw new Error('Failed to parse checkout data');
        }

        const items = data.items.map((item: CartItem) => {
          const matchInfo = matchData.response?.find((match) =>
            match.teams.home.name === item.homeTeam &&
            match.teams.away.name === item.awayTeam
          );

          return {
            ...item,
            homeTeamLogo: matchInfo?.teams.home.logo || item.homeTeamLogo || defaultLogo,
            awayTeamLogo: matchInfo?.teams.away.logo || item.awayTeamLogo || defaultLogo
          };
        });

        setCheckoutItems(items);
        const total = items.reduce((sum: number, item: CartItem) => sum + (item.price || 160), 0);
        setTotalAmount(total);
      } catch (error) {
        console.error('Error parsing checkout data:', error);
        // Only navigate if the user hasn't started filling the form
        if (!cardNumber && !cardholderName && !expirationDate && !cvv) {
          toast.error('Invalid checkout data');
          navigate('/cart');
        }
      }
    };

    loadCheckoutItems();
  }, [navigate]);

  const handlePayment = async () => {
    if (!user) {
      toast.error('Please log in to complete the purchase');
      navigate('/login');
      return;
    }

    if (!validateForm()) {
      return;
    }

    setIsProcessing(true);
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        throw new Error('Authentication token not found');
      }

      const purchaseData = {
        cardType: selectedCardType,
        cardNumber: cardNumber.replace(/\s/g, ''),
        cardHolderName: cardholderName,
        expirationDate: expirationDate,
        cvvCode: cvv,
        cartItems: checkoutItems.map(item => ({
          cartItemId: item.cartItemId,
          matchName: `${item.homeTeam} vs ${item.awayTeam}`,
          matchDate: item.date.slice(0,16),
          seatNumber: item.seatNumber,
          venueName: item.venueName,
          venueCity: item.venueCity
        }))
      };

      const ticketResponse = await fetch('http://localhost:5001/api/tickets', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(purchaseData)
      });

      if (!ticketResponse.ok) {
        const errorData = await ticketResponse.json();
        throw new Error(errorData.message || 'Failed to create tickets');
      }

      const ticketData = await ticketResponse.json();

      // Clear cart and sessionStorage after successful payment
      clearCart();
      sessionStorage.removeItem('checkoutData');

      toast.success('Payment processed successfully!');

      // navigate('/confirmation', { 
      //   state: { 
      //     tickets: ticketData
      //   } 
      // });

      // Delay navigation slightly to allow state update to settle
      setTimeout(() => {
        console.log("Navigating to /facialrecognition (delayed)...");
        navigate("/facialrecognition", { replace: true });
      }, 0);
      
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'An unknown error occurred';
      console.error('Payment error:', error);
      toast.error('Payment failed: ' + errorMessage);
    } finally {
      setIsProcessing(false);
    }
  };

  return (
    <AuthComponent onAuthSuccess={() => setIsAuthSuccess(true)}>
      <div className="w-full max-w-7xl mx-auto px-4 py-8">
        <style>
          {`
            .checkout-container {
              width: 100%;
              height: calc(100vh - var(--navbar-height));
              max-width: 1536px;
              margin: 0 auto;
              padding: clamp(1rem, 2vw, 1.5rem);
              display: flex;
              flex-direction: column;
              gap: clamp(1rem, 2vw, 1.5rem);
            }

            .checkout-content {
              display: grid;
              grid-template-columns: 1fr;
              gap: clamp(1rem, 2vw, 1.5rem);
              flex: 1;
              min-height: 0;
              overflow: hidden;
            }

            @media (min-width: 768px) {
              .checkout-content {
                grid-template-columns: 1fr 1fr;
              }
            }

            .ticket-info, .payment-form {
              background: rgba(0, 0, 0, 0.8);
              border-radius: 16px;
              padding: clamp(1.5rem, 3vw, 2rem);
              color: white;
              backdrop-filter: blur(8px);
              border: 1px solid rgba(255, 255, 255, 0.1);
              overflow-y: auto;
              scrollbar-width: thin;
              scrollbar-color: rgba(255, 255, 255, 0.2) transparent;
            }

            .ticket-info::-webkit-scrollbar,
            .payment-form::-webkit-scrollbar {
              width: 6px;
            }

            .ticket-info::-webkit-scrollbar-thumb,
            .payment-form::-webkit-scrollbar-thumb {
              background-color: rgba(255, 255, 255, 0.2);
              border-radius: 3px;
            }

            .ticket-header, .form-header {
              font-size: clamp(1.25rem, 3vw, 1.5rem);
              font-weight: bold;
              margin-bottom: clamp(0.75rem, 2vw, 1rem);
              text-align: center;
            }

            .ticket-details {
              display: flex;
              flex-direction: column;
              gap: 0.75rem;
            }

            .detail-row {
              display: flex;
              justify-content: space-between;
              align-items: center;
              padding: 0.5rem 0;
              border-bottom: 1px solid rgba(255, 255, 255, 0.1);
            }

            .detail-label {
              color: rgba(255, 255, 255, 0.7);
              font-size: clamp(0.9rem, 1.8vw, 1rem);
            }

            .detail-value {
              font-weight: 500;
              font-size: clamp(0.9rem, 1.8vw, 1rem);
            }

            .card-types {
              display: grid;
              grid-template-columns: repeat(3, 1fr);
              gap: 0.5rem;
              margin-bottom: 1rem;
            }

            .card-type-button {
              position: relative;
              padding: clamp(0.75rem, 2vw, 1rem);
              border-radius: 0.75rem;
              font-weight: 700;
              font-size: clamp(1rem, 1.8vw, 1.2rem);
              letter-spacing: 0.5px;
              transition: all 0.2s ease;
              border: none;
              cursor: pointer;
              overflow: hidden;
              display: flex;
              align-items: center;
              justify-content: center;
              min-height: 4rem;
              box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            }

            .card-type-button::before {
              content: '';
              position: absolute;
              top: 0;
              left: 0;
              right: 0;
              bottom: 0;
              opacity: 0;
              transition: opacity 0.2s ease;
            }

            .card-type-button:hover::before {
              opacity: 1;
            }

            .card-type-button.visa {
              background: linear-gradient(135deg, #1A1F71 0%, #2B3190 100%);
              color: white;
              text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
            }

            .card-type-button.visa.selected {
              background: white;
              color: #1A1F71;
              text-shadow: none;
              border: 2px solid #1A1F71;
            }

            .card-type-button.mastercard {
              background: linear-gradient(135deg, #EB001B 0%, #FF5F00 50%, #F79E1B 100%);
              color: white;
              text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
            }

            .card-type-button.mastercard.selected {
              background: white;
              color: #EB001B;
              text-shadow: none;
              border: 2px solid #EB001B;
            }

            .card-type-button.verve {
              background: linear-gradient(135deg, #2D3092 0%, #4CAF50 100%);
              color: white;
              text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
            }

            .card-type-button.verve.selected {
              background: white;
              color: #2D3092;
              text-shadow: none;
              border: 2px solid #2D3092;
            }

            .card-type-button.selected {
              transform: translateY(-2px);
              box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
            }

            .card-type-button:hover {
              transform: translateY(-2px);
              box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
              opacity: 0.95;
            }

            .card-type-button:active {
              transform: translateY(0);
              box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            }

            .form-group {
              margin-bottom: 1rem;
            }

            .form-label {
              display: block;
              color: white;
              margin-bottom: 0.25rem;
              font-size: clamp(0.9rem, 1.8vw, 1rem);
            }

            .form-input {
              width: 100%;
              padding: clamp(0.75rem, 2vw, 1rem);
              border-radius: 0.5rem;
              border: 1px solid rgba(255, 255, 255, 0.2);
              background: rgba(255, 255, 255, 0.1);
              color: white;
              font-size: clamp(0.9rem, 1.8vw, 1rem);
              transition: all 0.2s ease;
            }

            .form-input::placeholder {
              color: rgba(255, 255, 255, 0.6);
              opacity: 1;
            }

            .form-input:focus {
              border-color: #10B981;
              outline: none;
            }

            .form-input:focus::placeholder {
              color: rgba(255, 255, 255, 0.4);
            }

            .form-row {
              display: grid;
              grid-template-columns: 1fr 1fr;
              gap: 1rem;
            }

            .submit-button {
              width: 100%;
              padding: clamp(0.5rem, 1.5vw, 0.75rem);
              border-radius: 0.5rem;
              background: #10B981;
              color: white;
              font-weight: 600;
              font-size: clamp(0.9rem, 1.8vw, 1rem);
              transition: all 0.2s ease;
              margin-top: 1rem;
            }

            .submit-button:hover:not(:disabled) {
              background: #059669;
              transform: translateY(-2px);
            }

            .submit-button:disabled {
              opacity: 0.7;
              cursor: not-allowed;
            }

            .back-link {
              display: inline-block;
              color: #60A5FA;
              text-decoration: none;
              font-size: clamp(0.9rem, 1.8vw, 1rem);
              transition: color 0.2s ease;
              margin-top: 0.75rem;
            }

            .back-link:hover {
              color: #3B82F6;
            }

            @media (max-width: 640px) {
              .form-row {
                grid-template-columns: 1fr;
              }
            }

            @media (max-height: 700px) {
              .checkout-container {
                gap: 0.75rem;
              }

              .form-group {
                margin-bottom: 0.75rem;
              }
            }

            .tickets-list {
              display: flex;
              flex-direction: column;
              gap: 1rem;
              margin-bottom: 1.5rem;
            }

            .ticket-card {
              position: relative;
              background: rgba(255, 255, 255, 0.05);
              border-radius: 0.75rem;
              padding: 1.25rem;
              transition: all 0.3s ease;
              border: 1px solid rgba(255, 255, 255, 0.1);
            }

            .ticket-card:hover {
              transform: translateY(-2px);
              box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
            }

            .ticket-card:hover .ticket-details-hover {
              opacity: 1;
              visibility: visible;
            }

            .ticket-preview {
              display: flex;
              align-items: center;
              justify-content: space-between;
              gap: 1rem;
            }

            .teams-section {
              display: flex;
              align-items: center;
              gap: 0.5rem;
            }

            .team-logo-small {
              width: 2rem;
              height: 2rem;
              object-fit: contain;
            }

            .vs-text-small {
              font-size: clamp(0.9rem, 1.8vw, 1rem);
              color: #10B981;
              font-weight: 600;
            }

            .match-info {
              flex: 1;
              display: flex;
              flex-direction: column;
            }

            .team-names {
              font-weight: 600;
              color: white;
              font-size: clamp(0.9rem, 1.8vw, 1rem);
            }

            .match-date {
              font-size: clamp(0.9rem, 1.8vw, 1rem);
              color: rgba(255, 255, 255, 0.7);
            }

            .price-tag {
              font-weight: 600;
              color: #10B981;
              font-size: clamp(0.9rem, 1.8vw, 1rem);
            }

            .ticket-details-hover {
              position: absolute;
              top: 100%;
              left: 0;
              right: 0;
              background: rgba(0, 0, 0, 0.95);
              border-radius: 0.75rem;
              padding: 1rem;
              opacity: 0;
              visibility: hidden;
              transition: all 0.3s ease;
              z-index: 10;
              margin-top: 0.5rem;
              border: 1px solid rgba(255, 255, 255, 0.1);
              box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
            }

            .hover-content h3 {
              font-size: clamp(1.1rem, 2vw, 1.25rem);
              font-weight: 600;
              margin-bottom: 0.75rem;
              color: white;
              text-align: center;
            }

            .total-section {
              margin-top: 1rem;
              padding-top: 1rem;
              border-top: 2px solid rgba(255, 255, 255, 0.1);
            }

            .total-row {
              display: flex;
              justify-content: space-between;
              align-items: center;
              font-size: clamp(1.25rem, 3vw, 1.5rem);
              font-weight: 600;
            }

            .total-label {
              color: rgba(255, 255, 255, 0.9);
            }

            .total-value {
              color: #10B981;
            }
          `}
        </style>

        <div className="checkout-container">
          <div className="checkout-content">
            <div className="ticket-info">
              <h2 className="ticket-header">Selected Tickets</h2>
              <div className="tickets-list">
                {checkoutItems.map((item, index) => {
                  const homeTeamName = (typeof item.homeTeam === 'object' && item.homeTeam !== null && 'name' in item.homeTeam) 
                    ? (item.homeTeam as Team).name 
                    : String(item.homeTeam);
                  
                  const awayTeamName = (typeof item.awayTeam === 'object' && item.awayTeam !== null && 'name' in item.awayTeam)
                    ? (item.awayTeam as Team).name
                    : String(item.awayTeam);
                  
                  const homeTeamLogo = (typeof item.homeTeam === 'object' && item.homeTeam !== null && 'logo' in item.homeTeam)
                    ? (item.homeTeam as Team).logo
                    : (item.homeTeamLogo || defaultLogo);
                  
                  const awayTeamLogo = (typeof item.awayTeam === 'object' && item.awayTeam !== null && 'logo' in item.awayTeam)
                    ? (item.awayTeam as Team).logo
                    : (item.awayTeamLogo || defaultLogo);

                  return (
                    <div key={index} className="ticket-card">
                      <div className="ticket-preview">
                        <div className="teams-section">
                          <img
                            src={homeTeamLogo}
                            alt={`${homeTeamName} logo`}
                            className="team-logo-small"
                            onError={(e) => {
                              const target = e.target as HTMLImageElement;
                              target.src = 'https://media.api-sports.io/football/teams/default.png';
                            }}
                          />
                          <span className="vs-text-small">VS</span>
                          <img
                            src={awayTeamLogo}
                            alt={`${awayTeamName} logo`}
                            className="team-logo-small"
                            onError={(bania) => {
                              const target = bania.target as HTMLImageElement;
                              target.src = 'https://media.api-sports.io/football/teams/default.png';
                            }}
                          />
                        </div>
                        <div className="match-info">
                          <span className="team-names">{homeTeamName} vs {awayTeamName}</span>
                          <span className="match-date">{formatMatchTime(item.date)}</span>
                        </div>
                        <div className="price-tag">${item.price}</div>
                      </div>
                      
                      <div className="ticket-details-hover">
                        <div className="hover-content">
                          <h3>Ticket Details</h3>
                          <div className="detail-row">
                            <span className="detail-label">Match</span>
                            <span className="detail-value">{`${homeTeamName} vs ${awayTeamName}`}</span>
                          </div>
                          <div className="detail-row">
                            <span className="detail-label">Date</span>
                            <span className="detail-value">{formatMatchTime(item.date)}</span>
                          </div>
                          <div className="detail-row">
                            <span className="detail-label">Seat</span>
                            <span className="detail-value">{item.seatNumber}</span>
                          </div>
                          <div className="detail-row">
                            <span className="detail-label">Venue</span>
                            <span className="detail-value">
                              {item.venueName}
                              {item.venueCity && `, ${item.venueCity}`}
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
              
              <div className="total-section">
                <div className="total-row">
                  <span className="total-label">Total Amount</span>
                  <span className="total-value">${totalAmount.toFixed(2)}</span>
                </div>
              </div>
            </div>

            <div className="payment-form">
              <h2 className="form-header">Payment Details</h2>
              
              <div className="card-types">
                <button 
                  className={`card-type-button visa ${selectedCardType === 'VISA' ? 'selected' : ''}`}
                  onClick={() => setSelectedCardType('VISA')}
                  type="button"
                >
                  VISA
                </button>
                <button 
                  className={`card-type-button mastercard ${selectedCardType === 'MASTERCARD' ? 'selected' : ''}`}
                  onClick={() => setSelectedCardType('MASTERCARD')}
                  type="button"
                >
                  MASTERCARD
                </button>
                <button 
                  className={`card-type-button verve ${selectedCardType === 'VERVE' ? 'selected' : ''}`}
                  onClick={() => setSelectedCardType('VERVE')}
                  type="button"
                >
                  VERVE
                </button>
              </div>

              <div className="form-group">
                <label className="form-label">Card Number</label>
                <input
                  type="text"
                  placeholder="**** **** **** ****"
                  className="form-input"
                  value={cardNumber}
                  onChange={handleCardNumberChange}
                  maxLength={19}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Cardholder Name</label>
                <input
                  type="text"
                  placeholder="Mohammed Lahkim"
                  className="form-input"
                  value={cardholderName}
                  onChange={(e) => setCardholderName(e.target.value.toUpperCase())}
                  required
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Expiration Date</label>
                  <input
                    type="text"
                    placeholder="MM/YY"
                    className="form-input"
                    value={expirationDate}
                    onChange={handleExpiryDateChange}
                    maxLength={5}
                    required
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">CVV Code</label>
                  <input
                    type="password"
                    placeholder="***"
                    className="form-input"
                    value={cvv}
                    onChange={handleCvvChange}
                    maxLength={3}
                    required
                  />
                </div>
              </div>

              <button
                onClick={handlePayment}
                className="submit-button"
                disabled={!isAuthSuccess || isProcessing}
              >
                {isProcessing ? 'Processing...' : `Pay $${totalAmount.toFixed(2)}`}
              </button>
            </div>
          </div>

          <Link to="/cart" className="back-link">
            ← Back to Cart
          </Link>
        </div>
      </div>
    </AuthComponent>
  );
};

export default Checkout;
