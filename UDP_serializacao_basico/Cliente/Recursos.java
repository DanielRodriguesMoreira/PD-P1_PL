import java.io.Serializable;

/**
 * Para enviar/receber objectos serializados as classes têm que implementar a interface Serializable
 */
public class Recursos implements Serializable {
    /**
     * A propriedade serialVersionUID é para garantir que as classes são serializadas/deserializadas da mesma forma.
     *
     * Lembrem-se do exemplo que dei na aula:
     *      serializamos um carro (objeto com 4 rodas e um motor), do outro lado, quando deserializamos e "montamos" o
     *      carro queremos que este tenha a mesma forma (caso contrário poderiamos estar a montar um trator visto que
     *      tem na mesma as 4 rodas e um motor).
     *
     * NOTA: só faz sentido definir e utilizar o mesmo serialVersionUID se as classes forem realmente parecidas (sem
     *       diferenças que causem erros na aplicação).
     */
    private static final long serialVersionUID = 1L;

    private String github;
    private String nonio;

    public String getGithub() {
        return this.github;
    }

    public String getNonio() {
        return this.nonio;
    }
}
