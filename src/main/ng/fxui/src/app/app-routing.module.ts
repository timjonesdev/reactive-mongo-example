import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MatchupComponent} from "./components/matchup/matchup.component";

const routes: Routes = [
  {path: 'matchup', component: MatchupComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
