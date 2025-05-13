export interface SimpleMatch {
  id: number;
  homeTeam: {
    name: string;
    logo: string;
  };
  awayTeam: {
    name: string;
    logo: string;
  };
  league?: {
    name: string;
  };
  date: string;
  venue: {
    name: string;
    city: string;
  };
}

export interface CartItem {
  id: number;
  cartItemId?: string; // Optional, for explicit UUID storage
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