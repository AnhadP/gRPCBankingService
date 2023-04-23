package com.anhad.server;

import com.anhad.models.Balance;
import com.anhad.models.DepositRequest;
import io.grpc.stub.StreamObserver;

public class CashStreamingRequest implements StreamObserver<DepositRequest> {

    private StreamObserver<Balance> balanceStreamObserver;
    private int accountBalance;
    @Override
    public void onNext(DepositRequest depositRequest) {
        int accountNumber = depositRequest.getAccountNumber();
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {

    }
}
