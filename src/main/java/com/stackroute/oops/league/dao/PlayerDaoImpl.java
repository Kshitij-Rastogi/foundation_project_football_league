package com.stackroute.oops.league.dao;

import com.stackroute.oops.league.exception.PlayerAlreadyExistsException;
import com.stackroute.oops.league.exception.PlayerNotFoundException;
import com.stackroute.oops.league.model.Player;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is implementing the PlayerDao interface This class has one field
 * playerList and a String constant for storing file name
 */
public class PlayerDaoImpl implements PlayerDao {
    private static final String PLAYER_FILE_NAME = "src/main/resources/player.csv";
    private List<Player> playerList;

    /**
     * Constructor to initialize an empty ArrayList for playerList
     */
    public PlayerDaoImpl() {
        playerList = new ArrayList<Player>();
    }

    /**
     * Return true if player object is stored in "player.csv" as comma separated
     * fields successfully when password length is greater than six and yearExpr is
     * greater than zero
     * 
     * @throws FileNotFoundException
     */
    @Override
    public boolean addPlayer(Player player) throws PlayerAlreadyExistsException{   
        boolean flag = false;
        Player findPlayer = null;
        try {
            findPlayer = findPlayer(player.getPlayerId());
        } catch (PlayerNotFoundException e) {
            e.printStackTrace();
        }
        if (findPlayer != null)
            throw new PlayerAlreadyExistsException();
        if (player.getPassword().length() > 6 && player.getYearExpr() > 0) {
            try {
                FileWriter fWriter = new FileWriter(PLAYER_FILE_NAME, true);
                fWriter.append(player.getPlayerId() + ", " + player.getPlayerName() + ", " + player.getPassword() + ", "
                        + player.getYearExpr() + ", " + player.getTeamTitle() + "\n");
                fWriter.close();
                flag = true;
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return flag;    
    }

    //Return the list of player objects by reading data from the file "player.csv"
    @Override
    public List<Player> getAllPlayers() {
        String line = null;
        List<Player> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PLAYER_FILE_NAME))) {
            while ((line = br.readLine()) != null) {
                String arr[] = line.split(", ");
                Player player = new Player(arr[0], arr[1], arr[2], Integer.parseInt(arr[3]));
                player.setTeamTitle(arr[4]);
                playerList.add(player);
                list.add(player);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        // System.out.println(list.size());
        return list;
    }

    /**
     * Return Player object given playerId to search
     */
    @Override
    public Player findPlayer(String playerId) throws PlayerNotFoundException {
        if(playerId==null || playerId=="" || playerId=="  ")
        throw new PlayerNotFoundException();
        List<Player> list = getAllPlayers();
        Player player = null;
        for (Player i : list) {
            if ((i.getPlayerId()).equals(playerId)) {
                player = new Player(i.getPlayerId(), i.getPlayerName(), i.getPassword(), i.getYearExpr());
                player.setTeamTitle(i.getTeamTitle());
            }
        }
        if (player == null) {
            throw new PlayerNotFoundException();
        }
        return player;

    }
}
