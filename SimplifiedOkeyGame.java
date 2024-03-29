import java.util.ArrayList;
import java.util.Random;

public class SimplifiedOkeyGame {

    Player[] players;
    Tile[] tiles;
    int tileCount;

    Tile lastDiscardedTile;

    int currentPlayerIndex = 0;
    public SimplifiedOkeyGame() {
        players = new Player[4];
     }


    public void createTiles() {
        tiles = new Tile[104];
        int currentTile = 0;

        // four copies of each value, no jokers
        for (int i = 1; i <= 26; i++) {
            for (int j = 0; j < 4; j++) {
                tiles[currentTile++] = new Tile(i);
            }
        }

        tileCount = 104;
    }

    /*
     * TODO: distributes the starting tiles to the players
     * player at index 0 gets 15 tiles and starts first
     * other players get 14 tiles, this method assumes the tiles are already shuffled
     */
    public void distributeTilesToPlayers() 
    {
        players[0].addTile(tiles[tiles.length - tileCount--]);

        for(int i = 1; i < (tiles.length / 2) + 5; i++)
        {
            players[(int)((i - 1) / 14)].addTile(tiles[tiles.length - tileCount--]);
            
            lastDiscardedTile = tiles[tiles.length - tileCount + 1];
        }
    }

    /*
     * TODO: get the last discarded tile for the current player
     * (this simulates picking up the tile discarded by the previous player)
     * it should return the toString method of the tile so that we can print what we picked
     */
    public String getLastDiscardedTile() 
    {
        players[currentPlayerIndex].addTile(lastDiscardedTile);
        return lastDiscardedTile.toString();
    }

    /*
     * TODO: get the top tile from tiles array for the current player
     * that tile is no longer in the tiles array (this simulates picking up the top tile)
     * and it will be given to the current player
     * returns the toString method of the tile so that we can print what we picked
     */
    public String getTopTile() 
    {
        players[currentPlayerIndex].addTile(tiles[tiles.length - tileCount]);
        return tiles[tiles.length - tileCount--].toString();
    }

    /*
     * TODO: should randomly shuffle the tiles array before game starts
     */
    public void shuffleTiles() {
        Random rand = new Random();
        for(int i = 0;i<tiles.length;i++)
        {
            int changingindex = rand.nextInt(tiles.length);
            Tile temp = tiles[changingindex];
            tiles[changingindex] = tiles[i];
            tiles[i] = temp;
        }
    }

    /*
     * TODO: check if game still continues, should return true if current player
     * finished the game. use checkWinning method of the player class to determine
     */
    public boolean didGameFinish() {
        if(!hasMoreTileInStack())
        {
            return true;
        }
        else if(players[currentPlayerIndex].checkWinning())
        {
            return true;
        }
        return false;
    }

    /* TODO: finds the player who has the highest number for the longest chain
     * if multiple players have the same length may return multiple players
     */
    public ArrayList<Player> getPlayerWithHighestLongestChain() {
        ArrayList<Player> winners = new ArrayList<>();
        int longestchain = players[0].findLongestChain();
        for(int i = 0;i<players.length;i++)
        {
            if(players[i].findLongestChain() > longestchain)
            {
                longestchain = players[i].findLongestChain();
            }
        }
        
        for(int i = 0;i<players.length;i++)
        {
            if(players[i].findLongestChain()== longestchain)
            {
                winners.add(players[i]);
            }
        }
        return winners;
    }
    
    /*
     * checks if there are more tiles on the stack to continue the game
     */
    public boolean hasMoreTileInStack() {
        return tileCount != 0;
    }

    /*
     * TODO: pick a tile for the current computer player using one of the following:
     * - picking from the tiles array using getTopTile()
     * - picking from the lastDiscardedTile using getLastDiscardedTile()
     * you should check if getting the discarded tile is useful for the computer
     * by checking if it increases the longest chain length, if not get the top tile
     */
    public void pickTileForComputer() {
        Player currentComputer = players[currentPlayerIndex];

        int currentLongestChain = currentComputer.findLongestChain();

        currentComputer.addTile(lastDiscardedTile);

        int newLongestChain = currentComputer.findLongestChain();

        currentComputer.getAndRemoveTile(14); //??

        if(newLongestChain > currentLongestChain && lastDiscardedTile != null)
        {
            getLastDiscardedTile();
        }
        else
        {
            getTopTile();
        }
    }

    /*
     * TODO: Current computer player will discard the least useful tile.
     * you may choose based on how useful each tile is
     */
    public void discardTileForComputer() {
         Player currentComputer = players[currentPlayerIndex];

        Tile[] currentComputerTiles = currentComputer.getTiles();
        Tile prevTile = currentComputerTiles[0];
        boolean keepGoing = true;

        for(int i = 1; i < currentComputerTiles.length; i++)
        {
            Tile nextTile = currentComputerTiles[i];

            if(nextTile.compareTo(prevTile) == 0)
            {
                currentComputer.getAndRemoveTile(i);
                i = currentComputerTiles.length;
                lastDiscardedTile = nextTile;
                keepGoing = false;
            }

            else
            {
                prevTile = nextTile;
            }
        }

        if(keepGoing)
        {
            int longestChain = currentComputer.findLongestChain();
            
            for(int j = 0; j < currentComputerTiles.length; j++)
            {
                Tile removedTile = currentComputer.getAndRemoveTile(j);
                int newChain = currentComputer.findLongestChain();
                currentComputer.addTile(removedTile);

                if(newChain == longestChain)
                {
                    lastDiscardedTile = currentComputer.getAndRemoveTile(j);
                    j = currentComputerTiles.length;
                }
            }
        }
    }

    /*
     * TODO: discards the current player's tile at given index
     * this should set lastDiscardedTile variable and remove that tile from
     * that player's tiles
     */
    public void discardTile(int tileIndex) {
        Player current = players[currentPlayerIndex];

        Tile tileToDiscard = current.getAndRemoveTile(tileIndex);

        lastDiscardedTile = tileToDiscard;
    }

    public void displayDiscardInformation() {
        if(lastDiscardedTile != null) {
            System.out.println("Last Discarded: " + lastDiscardedTile.toString());
        }
    }

    public void displayCurrentPlayersTiles() {
        players[currentPlayerIndex].displayTiles();
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

      public String getCurrentPlayerName() {
        return players[currentPlayerIndex].getName();
    }

    public void passTurnToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % 4;
    }

    public void setPlayerName(int index, String name) {
        if(index >= 0 && index <= 3) {
            players[index] = new Player(name);
        }
    }

}
