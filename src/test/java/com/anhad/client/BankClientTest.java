package com.anhad.client;

import com.anhad.models.*;
import com.anhad.models.BankServiceGrpc;
import com.anhad.server.BankService;
import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.anhad.models.BankServiceGrpc.newBlockingStub;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) //when creating static variables, it is very difficult so this is used to create instance variables
public class BankClientTest {

    private BankServiceGrpc.BankServiceBlockingStub blockingStub;
    private BankServiceGrpc.BankServiceStub bankServiceStub;

    @BeforeAll //invoked before all
    public void setup() {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 8081)
                .usePlaintext()
                .build();
        this.blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
        this.bankServiceStub = BankServiceGrpc.newStub(managedChannel);
    }

    @Test
    public void balanceTest() {

        BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder()
                .setAccountNumber(7)
                .build();

        Balance balance = this.blockingStub.getBalance(balanceCheckRequest);
        System.out.println(
                "Received: " + balance.getAmount()
        );
    }

    @Test
    public void withdrawTest() {

        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder()
                .setAccountNumber(7)
                .setAmount(40)
                .build();
        this.blockingStub.withdraw(withdrawRequest)
                .forEachRemaining(money -> System.out.println("Received : " + money.getValue()));
    }

    @Test
    public void withdrawAsyncTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder()
                .setAccountNumber(10)
                .setAmount(60)
                .build();
        this.bankServiceStub.withdraw(withdrawRequest, new MoneyStreamingResponse(latch));
        latch.await();
    }
}
