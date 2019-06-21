import {Injectable, NgZone} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {BehaviorSubject, Observable} from "rxjs";
import {TeamModel} from "../models/team.model";

@Injectable({
  providedIn: 'root'
})
export class TeamService {
  private teamsWatchUrl = environment.backendUrl + environment.watchTeamsPath;
  private teamWatchUrl = environment.backendUrl + environment.watchTeamPath;
  private allTeamsUrl = environment.backendUrl + environment.getAllTeamsPath;
  private updatePlayerUrl = environment.backendUrl + environment.updatePlayerPath;

  private teamsSource = new BehaviorSubject([]);
  _teamsSource: Observable<TeamModel[]> = this.teamsSource.asObservable();

  private teamWatchSource = new BehaviorSubject(new TeamModel());
  _teamWatchSource: Observable<TeamModel> = this.teamWatchSource.asObservable();

  constructor(private http: HttpClient, private zone: NgZone) {

    this.getTeams().subscribe(data => {
        let teams = data as TeamModel[];
        this.teamsSource.next(teams);
      }, error => console.log('Error: ' + error),
      () => console.log('done loading teams'));

    this.getTeamsStream().subscribe(data => {
        this.teamWatchSource.next(new TeamModel().deserialize(data));
      }, error => console.log('Error: ' + error),
      () => console.log('done loading team stream'));
  }

  /**
   * Get the initial set of teams
   */
  getTeams(): Observable<TeamModel[]> {
    return this.http.get<TeamModel[]>(this.allTeamsUrl);
  }

  /**
   * Send a score update for a particular player, by name
   * @param playerName - the name of the player to update
   * @param scoreChange - the change (+/-) in score
   */
  updatePlayer(playerName: string, scoreChange: number): void {
    let url = this.updatePlayerUrl.replace('{name}', playerName);
    url = url.replace('{scoreChange}', '' + scoreChange);

    this.http.get(url).subscribe();
  }

  /**
   * Subscribe to the teams update Server Sent Event stream
   */
  getTeamsStream(): Observable<TeamModel> {
    return Observable.create((observer) => {
      let url = this.teamsWatchUrl;
      let eventSource = new EventSource(url);

      eventSource.onmessage = (event) => {
        let json = JSON.parse(event.data);
        if (json !== undefined && json !== '') {
          this.zone.run(() => observer.next(json));
        }
      };

      eventSource.onerror = (error) => {
        if (eventSource.readyState === 0) {
          console.log('The stream has been closed by the server.');
          eventSource.close();
          observer.complete();
        } else {
          observer.error('EventSource error: ' + error);
        }
      }
    });
  }

}
