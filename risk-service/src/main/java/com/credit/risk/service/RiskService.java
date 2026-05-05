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
        int count = 1 + random.nextInt(5);
        List<DebtInfo> debts = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            double monto = Math.round((500 + random.nextDouble() * 9500) * 100.0) / 100.0;
            double mensualidad = Math.round(monto * (0.05 + random.nextDouble() * 0.10) * 100.0) / 100.0;
            String entidad = ENTIDADES[random.nextInt(ENTIDADES.length)];

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

    public int calculateScore(String cedula, double montoSolicitado, double salario, int tiempoAnios) {
        List<DebtInfo> debts = generateDebts(cedula);

        double deudaMensual = debts.stream()
            .mapToDouble(DebtInfo::getMensualidad)
            .sum();

        double cuotaMensual = montoSolicitado / (tiempoAnios * 12.0);

        double limiteEndeudamiento = salario * 0.40;

        int ptsEndeudamiento = calcularPuntosEndeudamiento(deudaMensual, limiteEndeudamiento);
        int ptsCuota = calcularPuntosCuota(cuotaMensual, salario);
        int ptsCarga = calcularPuntosCarga(debts.size());
        int ptsVariante = 1 + ThreadLocalRandom.current().nextInt(15);

        int score = ptsEndeudamiento + ptsCuota + ptsCarga + ptsVariante;
        return Math.max(1, Math.min(100, score));
    }

    private int calcularPuntosEndeudamiento(double deudaMensual, double limite) {
        if (limite <= 0) return 0;
        double ratio = deudaMensual / limite;
        if (ratio <= 0.30) return 40;
        if (ratio <= 0.60) return 30;
        if (ratio <= 0.80) return 20;
        if (ratio <= 1.00) return 10;
        return 0;
    }

    private int calcularPuntosCuota(double cuotaMensual, double salario) {
        if (salario <= 0) return 0;
        double ratio = cuotaMensual / salario;
        if (ratio <= 0.15) return 30;
        if (ratio <= 0.25) return 22;
        if (ratio <= 0.35) return 14;
        if (ratio <= 0.40) return 6;
        return 0;
    }

    private int calcularPuntosCarga(int numDeudas) {
        if (numDeudas <= 0) return 15;
        if (numDeudas == 1) return 12;
        if (numDeudas == 2) return 9;
        if (numDeudas == 3) return 6;
        if (numDeudas == 4) return 3;
        return 0;
    }
}
