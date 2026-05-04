package com.credit.risk.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.credit.risk.grpc.DebtInfo;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RiskService {

    private static final String[] ENTIDADES = {
        "Banco de Guayaquil", "Banco Pichincha", "Banco Austro",
        "Banco Bolivariano", "Cooperativa JEP", "Cooperativa Alianza"
    };

    public List<DebtInfo> generateDebts(String cedula) {
        var random = ThreadLocalRandom.current();
        int seed = cedula.hashCode();
        var seededRandom = new java.util.Random(seed);
        int count = 1 + seededRandom.nextInt(5);
        List<DebtInfo> debts = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            double monto = Math.round((500 + seededRandom.nextDouble() * 9500) * 100.0) / 100.0;
            double mensualidad = Math.round(monto * (0.05 + seededRandom.nextDouble() * 0.10) * 100.0) / 100.0;
            String entidad = ENTIDADES[seededRandom.nextInt(ENTIDADES.length)];

            DebtInfo debt = DebtInfo.newBuilder()
                    .setCedula(cedula)
                    .setEntidad(entidad)
                    .setMonto(monto)
                    .setMensualidad(mensualidad)
                    .build();
            debts.add(debt);
        }

        return debts;
    }
}
