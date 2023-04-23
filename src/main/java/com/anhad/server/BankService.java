package com.anhad.server;

import com.anhad.models.*;
import com.anhad.models.Balance;
import com.anhad.models.BalanceCheckRequest;
import com.anhad.models.BankServiceGrpc;
import com.anhad.models.WithdrawRequest;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

    @Override
    public void getBalance(BalanceCheckRequest request, StreamObserver<Balance> responseObserver) {

        int accountNumber = request.getAccountNumber();
        Balance balance = Balance.newBuilder()
                .setAmount(AccountDatabase.getBalance(accountNumber))
                .build();

        responseObserver.onNext(balance);
        responseObserver.onCompleted();
    }

    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {
        int accountNumber = request.getAccountNumber();
        int amount = request.getAmount(); //Multiples of 10.
        int balance = AccountDatabase.getBalance(accountNumber);

        if(amount > balance) {
            Status status = Status.FAILED_PRECONDITION.withDescription("Not enough money. You have only " + balance + "dollars in your account");
            responseObserver.onError(status.asRuntimeException());
            return;
        }
        //all the validations are passed successfully
        for (int i = 0; i < (amount/10); i++) {
            Money money = Money.newBuilder()
                    .setValue(10)
                    .build();
            responseObserver.onNext(money);
            AccountDatabase.deductBalance(accountNumber, 10);
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<DepositRequest> cashDeposit(StreamObserver<Balance> responseObserver) {
        return super.cashDeposit(responseObserver);
    }
}
