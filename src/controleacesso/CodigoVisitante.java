package controleacesso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Representa um código de acesso do tipo visitante.
 * Formato: VIS-AAA9999-X
 * Onde:
 *   - AAA = letras maiúsculas
 *   - 9999 = números
 *   - X = soma dos 4 números mod 10
 */
public class CodigoVisitante implements CodigoAcesso {

    private final String codigo;
    private boolean usado;

    // Regex: VIS-AAA9999-X
    private static final Pattern PADRAO = Pattern.compile("^VIS-([A-Z]{3})(\\d{4})-(\\d)$");

    public CodigoVisitante(String codigo) {
        this.codigo = codigo;
        this.usado = false;

        // Validação no construtor
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

        String numeros = m.group(2);
        int soma = 0;
        for (char c : numeros.toCharArray()) {
            soma += Character.getNumericValue(c);
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
