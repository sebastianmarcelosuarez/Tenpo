package com.example.demo;


import com.example.demo.db.table.Historial;
import com.example.demo.request.HistoryRequest;
import com.example.demo.request.SumRequest;
import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;


@RestController
public class Controller {

    Integer latestPercentageValue;
    Integer requestAllowed = 3;




    /**
     * Debes desarrollar una API REST en Spring Boot utilizando java 11 o superior, con las siguientes funcionalidades:
     *
     * 1) Debe contener un servicio llamado por api-rest que reciba 2 números, los sume, y le aplique una suba de un porcentaje que
     * debe ser adquirido de un servicio externo (por ejemplo, si el servicio recibe 5 y 5 como valores, y el porcentaje devuelto por
     * el servicio externo es 10,
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
     * 2) Historial de todos los llamados a todos los endpoint junto con la respuesta en caso de haber sido exitoso.
     *      Responder en Json, con data paginada.
     *      //FALTA ESTO!, HACERLO VIA THREAD!!!!!!!!
     *      El guardado del historial de llamadas no debe sumar tiempo al servicio invocado,
     *      y en caso de falla, no debe impactar el llamado al servicio principal.
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
    ResponseEntity suma(@RequestBody SumRequest sumRequest) {

        if (this.requestAllowed == 0){
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        }

        try{
            this.requestAllowed--;
            Integer porcentualResult = calculatePercentageValue(sumRequest);
            return new ResponseEntity<>(porcentualResult, HttpStatus.OK);
        } catch (TimeoutException e) {
            return  ResponseEntity
                    .status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Error Message due timeOut");
        } catch (Exception e) {
            return  ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Generic Exception: " + e.getMessage());
        }finally {
            this.requestAllowed++;
        }
    }

    @CrossOrigin
    @GetMapping("/calculator/history")
     ResponseEntity history(@RequestBody HistoryRequest historyRequest) {

        try{
            List porcentualResult = getSumHistory(historyRequest);
            return new ResponseEntity<List<Historial>>(porcentualResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    List getSumHistory(HistoryRequest historyRequest) {
        Session session = getSession();

        String hql = "from Historial";
        Query query = session.createQuery(hql, Historial.class);

        int pageNumber = 1;
        if (historyRequest.getCurrentPage() != null && historyRequest.getCurrentPage() >= 0 ) {
            pageNumber =  historyRequest.getCurrentPage();
        }
        int pageSize = 20;
        int firstResult = (pageNumber - 1) * pageSize;

        query.setFirstResult(firstResult);
        query.setMaxResults(pageSize);

        return  query.list();
    }

    Integer calculatePercentageValue(SumRequest sumRequest) throws TimeoutException {
       Integer percentage;
            try {
                percentage = getFuturePercentage();
            } catch (TimeoutException e) {
                if (latestPercentageValue == null) {
                    throw new TimeoutException("TimeoutException message");
            } else {
                    // getLatestValueSaved
                    percentage = latestPercentageValue;
                }

            }

        Integer sumados =  Integer.sum(sumRequest.getNumberOne(), sumRequest.getNumberTwo());

        //save latestValue
        latestPercentageValue = percentage;

        Historial historial = new Historial();
        historial.setValue1(sumRequest.getNumberOne());
        historial.setValue2(sumRequest.getNumberTwo());
        historial.setPercentage(percentage);
        historial.setResult(sumados + ( (sumados * percentage) / 100));
        historial.setURL("/calculator/sum");

         ExecutorService executor
                = Executors.newSingleThreadExecutor();
             executor.submit(() -> {
                 try{
                     saveHistoricalResults(historial);
                 } catch (Exception e) {
                     System.out.println("error while saving history: " + e.getMessage());
                 }

             });


        return sumados + ( (sumados * percentage) / 100);
    }

    void saveHistoricalResults(Historial historial) {
        Session session = getSession();
        session.beginTransaction();
        session.persist(historial);
        session.getTransaction().commit();
    }

    private Session getSession() {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        configuration.addAnnotatedClass(Historial.class);

        SessionFactory sessionFactory
                = configuration.buildSessionFactory();

       return sessionFactory.openSession();
    }

    Integer getFuturePercentage() throws TimeoutException {
        RetryPolicy<Integer> retry = RetryPolicy.<Integer>builder()
                .withDelay(Duration.ofMillis(300))
                .withMaxRetries(3)
                .onRetry(e -> System.out.println("Retry number" + e.getAttemptCount()))
                .onRetriesExceeded( e -> {throw new TimeoutException();})
                .build();

        return Failsafe.with(retry)
                .get(() -> getPercentageServiceMocked().get(3000, TimeUnit.MILLISECONDS));
    }

    private Future<Integer> getPercentageServiceMocked() {
         ExecutorService executor
                = Executors.newSingleThreadExecutor();
        return executor.submit(() -> {
            // use a variable to simulate a timeout exception
           //     Thread.sleep(1000);
                return ThreadLocalRandom.current().nextInt(1, 100);
            });
    }

}
