
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package controleacesso;

/**
 *
 * @author jamis
 */
public interface CodigoAcesso {
    
      /**
     * Retorna o código em formato String.
     * @return código completo (ex: VIS-ABC1234-5)
     */
    String getCodigo();

    /**
     * Indica se o código já foi utilizado.
     * @return true se usado, false se disponível
     */
    boolean isUsado();

    /**
     * Marca o código como utilizado.
     */
    void usar();

    /**
     * Verifica se o formato do código é válido
     * conforme as regras de cada classe que implementar.
     * @return true se válido, false caso contrário
     */
    boolean isValido();
    

    
    
}





