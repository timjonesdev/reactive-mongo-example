import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {HttpClientModule} from '@angular/common/http';
import {NavigationModule} from './modules/navigation/navigation.module';
import {TeamCardComponent} from './components/team-card/team-card.component';
import {MatButtonModule, MatCardModule, MatGridListModule, MatInputModule, MatListModule, MatToolbarModule} from "@angular/material";
import { MatchupComponent } from './components/matchup/matchup.component';
import { SimulationToolbarComponent } from './components/simulation-toolbar/simulation-toolbar.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";

@NgModule({
  declarations: [
    AppComponent,
    TeamCardComponent,
    MatchupComponent,
    SimulationToolbarComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    NavigationModule,
    MatCardModule,
    MatButtonModule,
    MatGridListModule,
    MatListModule,
    MatToolbarModule,
    MatInputModule,
    ReactiveFormsModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
