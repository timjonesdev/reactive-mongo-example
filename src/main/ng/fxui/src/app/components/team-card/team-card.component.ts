import {Component, Input, OnInit} from '@angular/core';
import {TeamModel} from "../../models/team.model";

@Component({
  selector: 'app-team-card',
  templateUrl: './team-card.component.html',
  styleUrls: ['./team-card.component.scss']
})
export class TeamCardComponent implements OnInit {

  @Input()
  team: TeamModel;

  constructor() {
  }

  ngOnInit() {
  }

}
