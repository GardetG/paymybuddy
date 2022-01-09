import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from '../authentication.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  email:string = "";
  password:string = "";

  constructor(private service:AuthenticationService, private router:Router) { }

  ngOnInit(): void {
  }

  doLogin() {
    let resp = this.service.login(this.email, this.password);
    resp.subscribe((data:any) => {
      console.log(data)
      this.router.navigate(["/home"])
    })
  }

}
