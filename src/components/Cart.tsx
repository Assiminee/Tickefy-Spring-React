import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { CartItem } from '../types/match';
import { toast } from 'react-toastify';
import { fetchAndStoreMatchData, getMatchData } from '../utils/matchDataService';

interface MatchTeam {
  name: string;
  logo: string;
}

interface MatchLeague {
  name: string;
}

interface TransformedCartItem {
  id: number;
  cartItemId: string;
  homeTeam: string;
  awayTeam: string;
  homeTeamLogo: string;
  awayTeamLogo: string;
  date: string;
  seatNumber: string;
  venueName: string;
  venueCity: string;
  price: number;
}

const Cart = () => {
  const navigate = useNavigate();
  const { cart, removeFromCart, clearCart, addToCart } = useCart();
  const [isLoading, setIsLoading] = useState(false);
  const [cartItems, setCartItems] = useState<TransformedCartItem[]>([]);
  const [selectedItems, setSelectedItems] = useState<Set<string>>(new Set());

  useEffect(() => {
    fetchCartItems();
  }, []);

  const fetchCartItems = async () => {
    try {
      setIsLoading(true);
      const token = localStorage.getItem('token');
      if (!token) {
        toast.error('Please log in to view your cart');
        navigate('/login');
        return;
      }

      // Fetch fresh match data to ensure logos are available
      await fetchAndStoreMatchData();
      const matchData = getMatchData();

      const response = await fetch('http://localhost:5001/api/cart', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        throw new Error('Failed to fetch cart items');
      }

      const data = await response.json();

      // Transform the backend data to match CartItem interface
      const transformedItems: TransformedCartItem[] = data.map((item: any) => {
        // Find matching match data to get logos
        const matchInfo = matchData.find(
          (match) =>
            match.homeTeam.name === item.homeTeamName &&
            match.awayTeam.name === item.awayTeamName
        );

        return {
          id: parseInt(item.id),
          cartItemId: item.id.toString(),
          homeTeam: item.homeTeamName || 'Unknown Team',
          awayTeam: item.awayTeamName || 'Unknown Team',
          homeTeamLogo: matchInfo?.homeTeam.logo || 'https://media.api-sports.io/football/teams/default.png',
          awayTeamLogo: matchInfo?.awayTeam.logo || 'https://media.api-sports.io/football/teams/default.png',
          date: item.matchDate || '',
          seatNumber: item.seatNumber?.toString() || 'N/A',
          venueName: item.venueName || 'Unknown Venue',
          venueCity: item.venueCity || 'Unknown City',
          price: 160
        };
      });

      // Update local state
      setCartItems(transformedItems);

      // Clear and update the cart context
      clearCart();
      transformedItems.forEach(item => {
        const cartItem: CartItem = {
          id: item.id,
          homeTeam: item.homeTeam,
          awayTeam: item.awayTeam,
          homeTeamLogo: item.homeTeamLogo,
          awayTeamLogo: item.awayTeamLogo,
          date: item.date,
          seatNumber: item.seatNumber,
          venueName: item.venueName,
          venueCity: item.venueCity,
          price: item.price
        };
        addToCart(cartItem);
      });

    } catch (error) {
      console.error('Error fetching cart:', error);
      toast.error('Failed to load cart items');
    } finally {
      setIsLoading(false);
    }
  };

  const handleRemoveFromCart = async (cartItemIds: string[]) => {
    try {
      if (cartItemIds.length === 0) {
        return;
      }

      const token = localStorage.getItem('token');
      if (!token) {
        toast.error('Please log in to remove items');
        return;
      }

      setIsLoading(true);

      const deletePromises = cartItemIds.map(async (cartItemId) => {
        try {
          const response = await fetch(`http://localhost:5001/api/cart/${cartItemId}`, {
            method: 'DELETE',
            headers: {
              'Authorization': `Bearer ${token}`
            }
          });

          if (!response.ok) {
            throw new Error(`Failed to remove item ${cartItemId}`);
          }

          setCartItems(prev => prev.filter(item => item.cartItemId !== cartItemId));
          removeFromCart(parseInt(cartItemId));
          
          return true;
        } catch (error) {
          console.error(`Error removing item ${cartItemId}:`, error);
          return false;
        }
      });

      const results = await Promise.all(deletePromises);
      const successCount = results.filter(Boolean).length;
      const failureCount = cartItemIds.length - successCount;

      setSelectedItems(new Set());

      if (successCount > 0) {
        toast.success(`${successCount} item${successCount > 1 ? 's' : ''} removed from cart`);
      }
      if (failureCount > 0) {
        toast.error(`Failed to remove ${failureCount} item${failureCount > 1 ? 's' : ''}`);
      }

      await fetchCartItems();
    } catch (error) {
      console.error('Error removing items:', error);
      toast.error('Failed to remove items from cart');
    } finally {
      setIsLoading(false);
    }
  };

  const handleItemSelect = (cartItemId: string) => {
    setSelectedItems(prevItems => {
      const newItems = new Set(prevItems);
      if (newItems.has(cartItemId)) {
        newItems.delete(cartItemId);
      } else {
        newItems.add(cartItemId);
      }
      return newItems;
    });
  };

  const handleSelectAll = () => {
    if (selectedItems.size === cartItems.length) {
      setSelectedItems(new Set());
    } else {
      setSelectedItems(new Set(cartItems.map(item => item.cartItemId)));
    }
  };

  const handlePurchase = async () => {
    try {
      if (selectedItems.size === 0) {
        toast.error('Please select at least one ticket to purchase');
        return;
      }

      setIsLoading(true);
      const token = localStorage.getItem('token');
      if (!token) {
        toast.error('Please log in to make a purchase');
        navigate('/login');
        return;
      }

      const itemsToPurchase = cartItems.filter(item => selectedItems.has(item.cartItemId));

      const purchaseData = {
        cardType: "VISA",
        cardNumber: "1234567890123456",
        cardHolderName: "John Doe",
        expirationDate: "12/25",
        cvvCode: "123",
        cartItems: itemsToPurchase.map(item => ({
          cartItemId: item.cartItemId,
          matchName: `${item.homeTeam} VS ${item.awayTeam}`,
          matchDate: formatDateForBackend(item.date),
          seatNumber: item.seatNumber,
          venueName: item.venueName,
          venueCity: item.venueCity
        }))
      };

      sessionStorage.setItem('checkoutData', JSON.stringify({
        items: itemsToPurchase,
        totalItems: itemsToPurchase.length,
        purchaseData: purchaseData
      }));

      navigate('/checkout');
      
    } catch (error) {
      console.error('Purchase error:', error);
      toast.error(error instanceof Error ? error.message : 'Failed to prepare purchase');
    } finally {
      setIsLoading(false);
    }
  };

  const formatDateForBackend = (dateString: string) => {
    const date = new Date(dateString);
    return date.toISOString().slice(0, 16);
  };

  const formatMatchTime = (dateString: string) => {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    }).format(date);
  };

  return (
    <>
      <style>
        {`
          .cart-container {
            width: 100%;
            max-width: 1200px;
            margin: 0 auto;
            padding: 2rem 1rem;
          }

          .cart-heading {
            color: white;
            font-size: 2rem;
            margin-bottom: 2rem;
            text-align: center;
          }

          .cart-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            grid-auto-rows: 240px;
            gap: 0.75rem;
            align-content: start;
          }

          .cart-card {
            background: rgba(0, 0, 0, 0.8);
            border-radius: 1rem;
            padding: 1rem;
            color: white;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            transition: transform 0.2s;
            height: 100%;
          }

          .cart-card:hover {
            transform: translateY(-5px);
          }

          .match-info {
            display: flex;
            justify-content: space-between;
            align-items: center;
            width: 100%;
            flex: none;
            height: 120px;
            overflow: hidden;
          }

          .team {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            flex: 1;
            height: 100%;
            overflow: hidden;
          }

          .team-logo {
            width: 48px;
            height: 48px;
            object-fit: contain;
            margin-bottom: 0.5rem;
          }

          .team-name {
            font-size: 0.9rem;
            text-align: center;
            font-weight: 500;
            line-height: 1.2;
            max-width: 100%;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
          }

          .vs {
            font-weight: bold;
            color: #4CAF50;
            display: flex;
            flex-direction: column;
            align-items: center;
            margin: 0 0.5rem;
          }

          .match-time {
            text-align: center;
            color: #9CA3AF;
            font-size: 0.85rem;
            margin: 0.5rem 0;
          }

          .button-container {
            width: 100%;
            margin-top: auto;
          }

          .cart-button {
            width: 100%;
            padding: 0.75rem;
            border: none;
            border-radius: 0.5rem;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.2s;
          }

          .delete-button {
            background: #DC2626;
            color: white;
          }

          .delete-button:hover {
            background: #B91C1C;
          }

          .empty-cart {
            text-align: center;
            color: white;
            padding: 3rem;
          }

          .browse-tickets-button {
            margin-top: 1rem;
            padding: 0.75rem 1.5rem;
            background: #4CAF50;
            color: white;
            border: none;
            border-radius: 0.5rem;
            cursor: pointer;
            transition: all 0.2s;
            font-weight: 500;
          }

          .browse-tickets-button:hover {
            background: #45a049;
            transform: translateY(-2px);
          }

          .purchase-button {
            position: fixed;
            bottom: 2rem;
            right: 2rem;
            padding: 1rem 2rem;
            background: #4CAF50;
            color: white;
            border: none;
            border-radius: 0.5rem;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.2s;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            z-index: 10;
            min-width: 150px;
          }

          .purchase-button:hover {
            background: #45a049;
            transform: translateY(-2px);
          }

          .purchase-button:disabled {
            background: #9CA3AF;
            cursor: not-allowed;
            transform: none;
          }

          @media (max-width: 1200px) {
            .cart-grid {
              grid-template-columns: repeat(3, 1fr);
            }
          }

          @media (max-width: 900px) {
            .cart-grid {
              grid-template-columns: repeat(2, 1fr);
            }
          }

          @media (max-width: 600px) {
            .cart-grid {
              grid-template-columns: 1fr;
            }
          }

          @media (max-height: 900px) {
            .cart-grid {
              grid-auto-rows: 180px;
            }
            .match-info {
              height: 90px;
            }
            .team-logo {
              width: 36px;
              height: 36px;
              margin-bottom: 0.25rem;
            }
            .team-name {
              font-size: 0.8rem;
            }
          }

          .checkbox-container {
            position: absolute;
            top: 1rem;
            left: 1rem;
            z-index: 1;
          }

          .checkbox {
            width: 20px;
            height: 20px;
            cursor: pointer;
          }

          .delete-selected-button {
            position: fixed;
            bottom: 2rem;
            left: 2rem;
            padding: 1rem 2rem;
            background: #DC2626;
            color: white;
            border: none;
            border-radius: 0.5rem;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.2s;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            z-index: 10;
          }

          .delete-selected-button:hover {
            background: #B91C1C;
            transform: translateY(-2px);
          }

          .delete-selected-button:disabled {
            background: #9CA3AF;
            cursor: not-allowed;
            transform: none;
          }

          .select-all-container {
            position: fixed;
            bottom: 2rem;
            right: 16rem;
            color: white;
            display: flex;
            align-items: center;
            gap: 0.75rem;
            z-index: 10;
            background: rgba(0, 0, 0, 0.7);
            padding: 0.75rem 1.5rem;
            border-radius: 0.5rem;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.1);
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1),
                       0 0 10px rgba(76, 175, 80, 0.2);
            transition: all 0.3s ease;
            white-space: nowrap;
            min-width: max-content;
          }

          @media (max-width: 768px) {
            .select-all-container {
              right: 2rem;
              bottom: 6rem;
            }

            .purchase-button {
              width: calc(100% - 4rem);
              right: 2rem;
            }
          }

          .action-buttons-container {
            position: fixed;
            bottom: 2rem;
            right: 2rem;
            display: flex;
            gap: 1rem;
            z-index: 10;
          }

          .futuristic-checkbox {
            appearance: none;
            width: 24px;
            height: 24px;
            border: 2px solid rgba(76, 175, 80, 0.5);
            border-radius: 6px;
            background: transparent;
            cursor: pointer;
            position: relative;
            transition: all 0.3s ease;
          }

          .futuristic-checkbox:checked {
            background: #4CAF50;
            border-color: #4CAF50;
          }

          .futuristic-checkbox:checked::after {
            position: absolute;
            left: 8px;
            top: 4px;
            width: 6px;
            height: 12px;
            border: solid white;
            border-width: 0 2px 2px 0;
            transform: rotate(45deg);
          }

          .futuristic-checkbox:hover {
            border-color: #4CAF50;
            box-shadow: 0 0 10px rgba(76, 175, 80, 0.3);
          }
        `}
      </style>

      <div
        className="fixed inset-0 flex flex-col bg-cover bg-center"
        style={{
          backgroundImage: "url('https://images.unsplash.com/photo-1508098682722-8b9510c4e1b2?ixlib=rb-4.0.3&auto=format&fit=crop&w=1350&q=80')",
        }}
      >
        <div className="flex-1 flex flex-col items-center pt-24 px-4">
          <div className="cart-container">
            {cartItems.length === 0 ? (
              <div className="empty-cart">
                <p>Your cart is empty</p>
                <button 
                  onClick={() => navigate('/ticketlist')}
                  className="browse-tickets-button"
                >
                  Browse Tickets
                </button>
              </div>
            ) : (
              <>
                <div className="cart-grid">
                  {cartItems.map((item) => (
                    <div key={`cart-item-${item.cartItemId}`} className="cart-card">
                      <div className="checkbox-container">
                        <input
                          type="checkbox"
                          className="checkbox"
                          checked={selectedItems.has(item.cartItemId)}
                          onChange={() => handleItemSelect(item.cartItemId)}
                        />
                      </div>

                      <div className="match-info">
                        <div className="team">
                          <img 
                            src={item.homeTeamLogo} 
                            alt={`${item.homeTeam} logo`} 
                            className="team-logo"
                            onError={(e) => {
                              const target = e.target as HTMLImageElement;
                              target.src = 'https://media.api-sports.io/football/teams/default.png';
                            }}
                          />
                          <span className="team-name">{item.homeTeam}</span>
                        </div>
                        <div className="vs">
                          <span>VS</span>
                        </div>
                        <div className="team">
                          <img 
                            src={item.awayTeamLogo} 
                            alt={`${item.awayTeam} logo`} 
                            className="team-logo"
                            onError={(e) => {
                              const target = e.target as HTMLImageElement;
                              target.src = 'https://media.api-sports.io/football/teams/default.png';
                            }}
                          />
                          <span className="team-name">{item.awayTeam}</span>
                        </div>
                      </div>
                      
                      <p className="match-time">{formatMatchTime(item.date)}</p>
                      <p className="match-time">Seat: {item.seatNumber}</p>
                    </div>
                  ))}
                </div>

                <div className="action-buttons-container">
                  <div className="select-all-container">
                    <input
                      type="checkbox"
                      className="futuristic-checkbox"
                      checked={selectedItems.size === cartItems.length}
                      onChange={handleSelectAll}
                    />
                    <span>Select All</span>
                  </div>

                  <button
                    className="purchase-button"
                    onClick={handlePurchase}
                    disabled={isLoading || selectedItems.size === 0}
                  >
                    {isLoading ? 'Processing...' : 'Proceed to Checkout'}
                  </button>
                </div>

                {selectedItems.size > 0 && (
                  <button
                    className="delete-selected-button"
                    onClick={() => handleRemoveFromCart(Array.from(selectedItems))}
                    disabled={isLoading}
                  >
                    {isLoading ? 'Deleting...' : `Delete Selected (${selectedItems.size})`}
                  </button>
                )}
              </>
            )}
          </div>
        </div>
      </div>
    </>
  );
};

export default Cart;