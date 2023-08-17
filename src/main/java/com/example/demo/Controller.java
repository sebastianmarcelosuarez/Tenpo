package com.example.demo;


import com.example.demo.request.SumRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


@RestController
public class Controller {


    /**
     * Debes desarrollar una API REST en Spring Boot utilizando java 11 o superior, con las siguientes funcionalidades:
     *
     * 1) Debe contener un servicio llamado por api-rest que reciba 2 números, los sume, y le aplique una suba de un porcentaje que
     * debe ser adquirido de un servicio externo (por ejemplo, si el servicio recibe 5 y 5 como valores, y el porcentaje devuelto por el servicio externo es 10,
     * entonces (5 + 5) + 10% = 11). Se deben tener en cuenta las siguientes consideraciones:
     *
     *      El servicio externo puede ser un mock, tiene que devolver el % sumado.
     *
     *      Dado que ese % varía poco, podemos considerar que el valor que devuelve ese servicio no va cambiar por 30 minutos.
     *
     *      Si el servicio externo falla, se debe devolver el último valor retornado. Si no hay valor, debe retornar un error la api.
     *
     *      Si el servicio falla, se puede reintentar hasta 3 veces.
     *
     * 2) Historial de todos los llamados a todos los endpoint junto con la respuesta en caso de haber sido exitoso. Responder en Json, con data paginada. El guardado del historial de llamadas no debe sumar tiempo al servicio invocado, y en caso de falla, no debe impactar el llamado al servicio principal.
     *
     * 3) La api soporta recibir como máximo 3 rpm (request / minuto), en caso de superar ese umbral, debe retornar un error con el código http y mensaje adecuado.
     *
     * 4) El historial se debe almacenar en una database PostgreSQL.
     *
     * 5) Incluir errores http. Mensajes y descripciones para la serie 4XX.
     *
     *
     */

    @CrossOrigin
    @GetMapping("/calculator/sum")
    private ResponseEntity<Integer>suma(@RequestBody SumRequest movieTitle) {
        try{
            Integer porcentualResult = calculatePercentageValue(movieTitle);
            return new ResponseEntity<Integer>(porcentualResult, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("error" +  e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private Integer calculatePercentageValue(SumRequest sumRequest) {

        Integer percentage = getPercentageMocked();
        Integer sumados =  Integer.sum(sumRequest.getNumberOne(), sumRequest.getNumberTwo());
        return sumados + ( (sumados * percentage) / 100);
    }

    private Integer getPercentageMocked() {
        // Mocked system, it may use a retrier ...
       return ThreadLocalRandom.current().nextInt(1, 100);
    }

}
