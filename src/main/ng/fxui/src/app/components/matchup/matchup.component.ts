import {Component, OnInit} from '@angular/core';
import {TeamModel} from "../../models/team.model";
import {TeamService} from "../../services/team.service";

@Component({
  selector: 'app-matchup',
  templateUrl: './matchup.component.html',
  styleUrls: ['./matchup.component.scss']
})
export class MatchupComponent implements OnInit {

  teams: TeamModel[] = [];

  constructor(private teamService: TeamService) {
  }

  ngOnInit() {
    this.loadTeams();
  }

  /**
   * Subscribe to the necessary backend data services
   */
  private loadTeams(): void {

    // subscribe to initial set of teams
    this.teamService._teamsSource.subscribe(value => {
      if (value !== undefined && value !== null) {
        this.teams = [].concat(value);
      }
    });

    // subscribe to changes in player team scores and player values
    this.teamService._teamWatchSource.subscribe(updatedTeam => {
      if (updatedTeam !== undefined && updatedTeam.name !== undefined) {
        let name = updatedTeam.name;
        let length = this.teams.length;

        for (let i = 0; i < length; i++) {
          if (this.teams[i].name === name) {
            this.teams[i] = updatedTeam;
          }
        }

        // trick to get the Angular event loop to pick up changes to array
        this.teams = [].concat(this.teams);
      }
    });
  }
}
