import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Connection } from 'src/app/model/Connection/connection.model';
import { Transaction } from 'src/app/model/Transaction/transaction.model';
import { ApiPaymybuddyService } from 'src/app/service/api-paymybuddy.service';
import { AuthenticationService } from 'src/app/service/authentication.service';
declare var bootstrap: any;

@Component({
  selector: 'app-transfer',
  templateUrl: './transfer.component.html',
  styleUrls: ['./transfer.component.css']
})
export class TransferComponent implements OnInit {

  userId:number = 0;
  connections: Connection[] = [];
  transactions: Transaction[] = [];
  request: Transaction = new Transaction();
  pages: Array<number> = new Array<number>(0);
  currentPage: number = 0;
  size: number = 3;
  error: string = "";
  requestTransactionForm:FormGroup = this.fb.group({
    "receiverId": [null, Validators.required],
    "amount": ['', [Validators.required, Validators.min(1), Validators.max(999.99)]]
  });
  confirmTransactionForm:FormGroup = this.fb.group({
    "description": ['', Validators.required]
  });

  constructor(private api:ApiPaymybuddyService, private fb:FormBuilder,private auth:AuthenticationService) { }

  ngOnInit(): void {
    this.userId = this.auth.getIdentity().userId
    this.loadConnections();
    this.loadTransactions()
  }

  loadConnections() {
    this.api.getAllConnections()
    .subscribe({
      next: (v) => {
        this.connections = v;
      }
    });
  }

  loadTransactions() {
    this.api.getPageOfTransactions(this.currentPage,this.size)
    .subscribe({
      next: (v) => {
        this.pages = new Array<number>(v.totalPages)
        this.transactions = v.content;
        this.transactions.map(transaction => {
          if (transaction.emitterId == 0) {
            transaction.emitterFirstname = "Deleted User";
          }
          if (transaction.receiverId ==0) {
            transaction.receiverFirstname = "Deleted User";
          }
        });
      }
    });
  }

  requestTransaction() { 
    if (this.requestTransactionForm.invalid) {
      return;
    }
    this.request = <Transaction>this.requestTransactionForm.value
    console.log(this.request)
    let receiver:Connection = this.connections.find(x => x.connectionId == this.request.receiverId)!
    this.request.receiverFirstname=receiver.firstname;
    this.request.receiverLastname=receiver.lastname;

    var modal = new bootstrap.Modal(document.getElementById('confirmTransactionModal'), {})
    modal.show();
  }

  confirmTransaction() {
    if (this.confirmTransactionForm.invalid) {
      return;
    }
    this.request =  {...this.request, ...<Transaction>this.confirmTransactionForm.value}
    this.api.requestTransaction(this.request)
    .subscribe({
      next: (v) => {
        this.loadTransactions();
        this.requestTransactionForm.reset();
        this.close();
      },
      error: (e) => {
        if (e.status == 404 || e.status == 409) {
          this.error = e.error;
        } else {
          this.error = "An error occured, please try again."
        }
      }
    });
  }

  close() {
    this.confirmTransactionForm.reset();
    var myModalEl = document.getElementById('confirmTransactionModal')
    var modal = bootstrap.Modal.getInstance(myModalEl) 
    modal.hide();
  }

  onPage(i: number) {
    this.currentPage = i;
    this.loadTransactions();
  }

  onPrec() {
    if (this.currentPage > 0) {
      this.onPage(this.currentPage - 1);
    }
  }

  onNext() {
    if (this.currentPage < this.pages.length-1) {
      this.onPage(this.currentPage + 1) ;
    }
  }

  check(form:FormGroup, controleName:string,error:string):boolean {
    let control = form.controls[controleName];
    if (control.hasError(error) && (control.touched || control.dirty)) {
      return true
    }
    return false;
  }
}