interface MatchData {
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
  venue?: {
    name: string;
    city: string;
  };
}

// Define MatchResponse to match API structure
interface MatchResponse {
  response: MatchData[];
}

export const getMatchData = (): MatchData[] => {
  try {
    const storedData = localStorage.getItem('matchData');
    if (!storedData) {
      return [];
    }
    return JSON.parse(storedData);
  } catch (error) {
    console.error('Error reading match data from localStorage:', error);
    return [];
  }
};

export const setMatchData = (data: MatchData[]) => {
  try {
    localStorage.setItem('matchData', JSON.stringify(data));
  } catch (error) {
    console.error('Error saving match data to localStorage:', error);
  }
};

export const fetchAndStoreMatchData = async (): Promise<MatchData[]> => {
  try {
    const token = localStorage.getItem('token');
    if (!token) {
      throw new Error('No token found');
    }

    const response = await fetch('http://localhost:5001/api/matchData', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (!response.ok) {
      throw new Error('Failed to fetch match data');
    }

    const data: MatchResponse | MatchData[] = await response.json();
    // Handle wrapped response (e.g., { response: MatchData[] })
    const matches: MatchData[] = 'response' in data ? data.response : data;
    setMatchData(matches);
    return matches;
  } catch (error) {
    console.error('Error fetching match data:', error);
    return getMatchData(); // Return cached data if fetch fails
  }
};