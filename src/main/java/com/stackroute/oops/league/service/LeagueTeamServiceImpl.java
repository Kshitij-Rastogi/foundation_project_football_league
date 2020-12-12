package com.stackroute.oops.league.service;

import com.stackroute.oops.league.dao.PlayerDao;
import com.stackroute.oops.league.dao.PlayerTeamDao;
import com.stackroute.oops.league.exception.PlayerAlreadyAllottedException;
import com.stackroute.oops.league.exception.PlayerAlreadyExistsException;
import com.stackroute.oops.league.exception.PlayerNotFoundException;
import com.stackroute.oops.league.exception.TeamAlreadyFormedException;
import com.stackroute.oops.league.model.Player;
import com.stackroute.oops.league.model.PlayerTeam;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class implements leagueTeamService
 * This has four fields: playerDao, playerTeamDao and registeredPlayerList and playerTeamSet
 */
public class LeagueTeamServiceImpl implements LeagueTeamService {
    PlayerDao playerDao;
    PlayerTeamDao playerTeamDao;
    List<Player> registeredPlayerList;
    Set<PlayerTeam> playerTeamSet;


    /**
     * Constructor to initialize playerDao, playerTeamDao
     * empty ArrayList for registeredPlayerList and empty TreeSet for playerTeamSet
     */
    public LeagueTeamServiceImpl() {
        playerDao = null;
        playerTeamDao = null;
        registeredPlayerList = new ArrayList<Player>();
        playerTeamSet = new TreeSet<PlayerTeam>();
    }

    //Add player data to file using PlayerDao object
    @Override
    public boolean addPlayer(Player player) throws PlayerAlreadyExistsException 
    {
        boolean flag = false;
        try{
            flag = playerDao.addPlayer(player);
        }
        catch(PlayerAlreadyExistsException e){
            throw new PlayerAlreadyExistsException();
        }
        return flag;
    }

    /**
     * Register the player for the given teamTitle
     * Throws PlayerNotFoundException if the player does not exists
     * Throws PlayerAlreadyAllottedException if the player is already allotted to team
     * Throws TeamAlreadyFormedException if the maximum number of players has reached for the given teamTitle
     * Returns null if there no players available in the file "player.csv"
     * Returns "Registered" for successful registration
     * Returns "Invalid credentials" when player credentials are wrong
     */
    @Override
    public synchronized String registerPlayerToLeague(String playerId, String password, LeagueTeamTitles teamTitle)
            throws PlayerNotFoundException, TeamAlreadyFormedException, PlayerAlreadyAllottedException
            {
                Player findPlayer=null;
                try{
                    findPlayer=playerDao.findPlayer(playerId);
                }
                catch (PlayerNotFoundException e){
                    throw new PlayerNotFoundException();
                }
                
                long countTeamMembers = registeredPlayerList.stream().filter(player->player.getTeamTitle().equals(teamTitle.getTeamTitle())).count();
                if(countTeamMembers > 12) {
                    throw new TeamAlreadyFormedException();
                }

                if(findPlayer!=null){
                    if(findPlayer.getPlayerId().equals(playerId)&&findPlayer.getPassword().equals(password)){
                        playerTeamSet=playerTeamDao.getAllPlayerTeams();
                        PlayerTeam players = new PlayerTeam(playerId,teamTitle.getTeamTitle());
                        if(playerTeamSet.contains(players)){
                            throw new PlayerAlreadyAllottedException();
                        }
                        else {
                            findPlayer.setTeamTitle(teamTitle.getTeamTitle());
                            registeredPlayerList.add(findPlayer);
                            return "Registered";
                        }
                    }
                    else{
                        return "Invalid credentials";
                    }
                }
                return null;
            }

    /**
     * Return the list of all registered players
     */
    @Override
    public List<Player> getAllRegisteredPlayers() {
        return registeredPlayerList;
    }


    /**
     * Return the existing number of players for the given title
     */
    @Override
    public int getExistingNumberOfPlayersInTeam(LeagueTeamTitles teamTitle) {
        int count = 0;
        for(Player p: registeredPlayerList){
            if(p.getTeamTitle().equals(String.valueOf(teamTitle))){
                count++;
            }
        }
        return count;
    }

    /**
     * Admin credentials are authenticated and registered players are allotted to requested teams if available
     * If the requested teams are already formed,admin randomly allocates to other available teams
     * PlayerTeam object is added to "finalteam.csv" file allotted by the admin using PlayerTeamDao
     * Return "No player is registered" when registeredPlayerList is empty
     * Throw TeamAlreadyFormedException when maximum number is reached for all teams
     * Return "Players allotted to teams" when registered players are successfully allotted
     * Return "Invalid credentials for admin" when admin credentials are wrong
     */
    @Override
    public String allotPlayersToTeam(String adminName, String password, LeagueTeamTitles teamTitle) 
    throws TeamAlreadyFormedException, PlayerNotFoundException {
        int countForTeam=0;
        if(!adminName.equals(AdminCredentials.admin)||!password.equals(AdminCredentials.password)) {
            return "Invalid credentials for admin";
        }
        if(registeredPlayerList.size()==0) {
            return "No player is registered";
        }

        //Team Already Formed
        List<Player> playerTeams=playerDao.getAllPlayers();
        for(int i=0;i<playerTeams.size();i++)
        {
            if(playerTeams.get(i).getTeamTitle()==null)
            {
                continue;
            }
            if(playerTeams.get(i).getTeamTitle().equals(teamTitle.getTeamTitle()))
            {
                countForTeam++;
            }
        }
        if(countForTeam==11) {
            throw new TeamAlreadyFormedException();
        }

        //Players Alloted
        int value=getExistingNumberOfPlayersInTeam(teamTitle);
        if (value < 12) {
            for (int j = 0; j < registeredPlayerList.size(); ) {
                playerTeamDao.addPlayerToTeam(registeredPlayerList.get(j));
                return "Players allotted to teams";
            }
        } else {
            int pick = new Random().nextInt(LeagueTeamTitles.values().length);
            LeagueTeamTitles pickTitle = LeagueTeamTitles.values()[pick];
            allotPlayersToTeam("admin", "pass", pickTitle);
        }
        return null;
    }


    /**
     * static nested class to initialize admin credentials
     * admin name='admin' and password='pass'
     */
    static class AdminCredentials {
        private static String admin = "admin";
        private static String password = "pass";
    }
}

