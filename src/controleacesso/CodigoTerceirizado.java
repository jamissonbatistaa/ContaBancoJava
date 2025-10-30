package controleacesso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Representa um código de acesso do tipo terceirizado.
 * Formato: TER-999-AAA-Y
 * Onde:
 *   - 999 = números
 *   - AAA = letras maiúsculas
 *   - Y = soma dos valores ASCII das letras mod 10
 */
public final class CodigoTerceirizado implements CodigoAcesso {

    private final String codigo;
    private boolean usado;

    // Regex: TER-999-AAA-Y
    private static final Pattern PADRAO = Pattern.compile("^TER-(\\d{3})-([A-Z]{3})-(\\d)$");

    public CodigoTerceirizado(String codigo) {
        this.codigo = codigo;
        this.usado = false;

        if (!isValido()) {
            throw new IllegalArgumentException("Formato inválido");
        }
    }

    @Override
    public String getCodigo() {
        return codigo;
    }

    @Override
    public boolean isUsado() {
        return usado;
    }

    @Override
    public void usar() {
        usado = true;
    }

    @Override
    public boolean isValido() {
        Matcher m = PADRAO.matcher(codigo);
        if (!m.matches()) return false;

        String letras = m.group(2);
        int soma = 0;
        for (char c : letras.toCharArray()) {
            soma += (int) c; // valor ASCII
        }
        int digitoVerificador = soma % 10;

        int ultimo = Integer.parseInt(m.group(3));
        return digitoVerificador == ultimo;
    }

    @Override
    public String toString() {
        return codigo + (usado ? " | USADO" : " | DISPONÍVEL");
    }
}
