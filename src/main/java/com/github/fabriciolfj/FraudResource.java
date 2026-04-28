package com.github.fabriciolfj;

import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.acme.stub.FraudDetection;
import org.acme.stub.FraudRes;
import org.acme.stub.TxDetails;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jspecify.annotations.NonNull;

@Path("/fraud")
public class FraudResource {

    @RestClient
    FraudDetectionService fraudDetectionService;

    @GrpcClient("fraud")
    FraudDetection fraudDetection;

    @GET
    @Path("/grpc/{id}")
    public Uni<FraudResponse> analyseFraudGrpc(@PathParam("id") String id) {
        var transaction = TxDetails.newBuilder()
                .setTxId(id)
                .setOnlineOrder(false)
                .setDistanceFromLastTransaction(100.00f)
                .setRatioToMedianPrice(20.00f)
                .setUsedChip(false)
                .setUsedPinNumber(false)
                .build();

        return fraudDetection.predict(transaction)
                .onItem()
                .transform(fr -> new FraudResponse(fr.getTxId(), fr.getFraud()));
    }

    @GET
    @Path("/{id}")
    public FraudResponse analyseFraud(@PathParam("id") String id) {
        TransactionRequest request = findTransaction(id);

        return fraudDetectionService.send(request);
    }

    private static @NonNull TransactionRequest findTransaction(String id) {
        return new TransactionRequest(
                id,
                100,
                10.99,
                true,
                true,
                false
        );
    }

}
