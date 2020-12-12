package com.stackroute.oops.league.dao;

import com.stackroute.oops.league.exception.PlayerNotFoundException;
import com.stackroute.oops.league.model.Player;
import com.stackroute.oops.league.model.PlayerTeam;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class implements the PlayerTeamDao interface This class has two fields
 * playerTeamSet,playerDao and a String constant for storing file name.
 */
public class PlayerTeamDaoImpl implements PlayerTeamDao {
    private static final String TEAM_FILE_NAME = "src/main/resources/finalteam.csv";
    Set<PlayerTeam> playerTeamSet;
    PlayerDao playerDao;

    /**
     * Constructor to initialize an empty TreeSet and PlayerDao object
     */
    public PlayerTeamDaoImpl() {
        playerTeamSet = new TreeSet<PlayerTeam>();
        playerDao = null;
    }

    /*
     * Returns the list of players belonging to a particular teamTitle by reading
     * from the file finalteam.csv
     */
    @Override
    public Set<PlayerTeam> getPlayerSetByTeamTitle(String teamTitle) {
        if (teamTitle != null) {
            try(BufferedReader br = new BufferedReader(new FileReader(TEAM_FILE_NAME))) {
                String line = null;
                while((line=br.readLine()) != null){
                    String[] arr = line.split(", ");
                    if(line.contains(teamTitle)){
                        for(int i=0; i<arr.length; i++){
                            PlayerTeam ptr = new PlayerTeam(arr[0], arr[1]);
                            playerTeamSet.add(ptr);
                        }
                    }      
                }    
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return playerTeamSet;   
        }
        return null;
    }

    //Add the given PlayerTeam Object to finalteam.csv file
    @Override
    public boolean addPlayerToTeam(Player player) throws PlayerNotFoundException 
    {
        boolean flag = false;
        Player findPlayer = null;
        try {
            findPlayer = playerDao.findPlayer(player.getPlayerId());
        } catch (PlayerNotFoundException e) {
            e.printStackTrace();
        }
        try{
            if(findPlayer != null){
                FileWriter fw = new FileWriter(TEAM_FILE_NAME);
                fw.append(player.getPlayerId() + ", " + player.getPlayerName() + ", " + player.getPassword() + ", "
                + player.getYearExpr() + ", " + player.getTeamTitle() + "\n");
                fw.close();
                flag = true;
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        return flag;
    }

    //Return the set of all PlayerTeam by reading the file content from finalteam.csv file
    @Override
    public Set<PlayerTeam> getAllPlayerTeams() {
        Set<PlayerTeam> playerTeam = new TreeSet<PlayerTeam>();
        String line= null;
        try(BufferedReader b = new BufferedReader(new FileReader(TEAM_FILE_NAME))){
            while((line = b.readLine()) != null){
                String[] temp = line.split(", ");
                PlayerTeam ptr = new PlayerTeam(temp[0], temp[4]);
                playerTeam.add(ptr);
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        return playerTeam;
    }
}
