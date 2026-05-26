import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Footer } from '../../../shared/components/footer/footer';
import { ScrollToTopComponent } from '../scroll-to-top.component/scroll-to-top.component';

@Component({
  selector: 'app-program',
  imports: [RouterOutlet, Footer, ScrollToTopComponent],
  templateUrl: './program.component.html',
  styleUrls: ['./program.component.css'],
  standalone: true
})
export class ProgramComponent {
}