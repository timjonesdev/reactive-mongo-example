import {Component, OnInit} from '@angular/core';
import {TeamService} from "../../services/team.service";

@Component({
  selector: 'app-simulation-toolbar',
  templateUrl: './simulation-toolbar.component.html',
  styleUrls: ['./simulation-toolbar.component.scss']
})
export class SimulationToolbarComponent implements OnInit {

  simulationCount = 15;

  constructor(private teamService: TeamService) {
  }

  ngOnInit() {
  }

  /**
   * Kick off the simulation on the backend
   * @param event - unused
   */
  private simulateClicked(event): void {
    this.teamService.randomSimulation(this.simulationCount);
  }

  /**
   * Call the 'reset' endpoint on the backend to make all scores go to zero.
   * @param event - unused
   */
  private resetClicked(event): void {
    this.teamService.resetScores();
  }
}
