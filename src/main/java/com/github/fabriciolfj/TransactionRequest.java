package com.github.fabriciolfj;

public record TransactionRequest(
        String txId,
        double distanceFomLastTransaction,
        double ratioToMedianPrice,
        boolean usedChip,
        boolean usePinNumber,
        boolean onlineOrder
) {}