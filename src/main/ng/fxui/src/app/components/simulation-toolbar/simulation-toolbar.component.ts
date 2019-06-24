import {Component, OnInit} from '@angular/core';
import {TeamService} from "../../services/team.service";

@Component({
  selector: 'app-simulation-toolbar',
  templateUrl: './simulation-toolbar.component.html',
  styleUrls: ['./simulation-toolbar.component.scss']
})
export class SimulationToolbarComponent implements OnInit {

  simulationCount = 5;

  constructor(private teamService: TeamService) {
  }

  ngOnInit() {
  }

  private simulateClicked(event): void {
    this.teamService.randomSimulation(this.simulationCount);
  }

  private resetClicked(event): void {
    this.teamService.resetScores();
  }
}
