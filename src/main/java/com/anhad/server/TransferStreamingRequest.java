package com.anhad.server;

import com.anhad.models.Account;
import com.anhad.models.TransferRequest;
import com.anhad.models.TransferResponse;
import com.anhad.models.TransferStatus;
import io.grpc.stub.StreamObserver;

public class TransferStreamingRequest implements StreamObserver<TransferRequest> {

    private StreamObserver <TransferResponse> transferResponseStreamObserver;

    public TransferStreamingRequest(StreamObserver<TransferResponse> transferResponseStreamObserver) {
        this.transferResponseStreamObserver = transferResponseStreamObserver;
    }

    @Override
    public void onNext(TransferRequest transferRequest) {
        int fromAccountNumber = transferRequest.getFromAccount();
        int toAccountNumber = transferRequest.getToAccount();
        int amount = transferRequest.getAmount();
        int balance = AccountDatabase.getBalance(fromAccountNumber);
        TransferStatus status = TransferStatus.FAILED;

        if(balance >= amount && fromAccountNumber != toAccountNumber) {
            AccountDatabase.addBalance(toAccountNumber, amount);
            AccountDatabase.deductBalance(fromAccountNumber, amount);
            status = TransferStatus.SUCCESS;
        }
        Account fromAccountInfo = Account.newBuilder().setAccountNumber(fromAccountNumber).setAmount(AccountDatabase.getBalance(fromAccountNumber)).build();
        Account toAccountInfo = Account.newBuilder().setAccountNumber(toAccountNumber).setAmount(AccountDatabase.getBalance(toAccountNumber)).build();

        TransferResponse transferResponse = TransferResponse.newBuilder()
                .setAccounts(1, fromAccountInfo)
                .setAccounts(2, toAccountInfo)
                .setStatus(status)
                .build();
        this.transferResponseStreamObserver.onNext(transferResponse);
    }

    @Override
    public void onError(Throwable throwableA) {

    }

    @Override
    public void onCompleted() {
        AccountDatabase.printAccountDetails();
        this.transferResponseStreamObserver.onCompleted();
    }
}
