# Tenpo API
API with springboot which saves into a postres DB

levantar ambos containers utilizando el comando 


      docker compose up

Este comando creara y cargara ambos containers, uno con la API 
y el otro con la DB

existen 2 endpoints:

    /calculator/sum
    /calculator/history

# /calculator/sum
Toma dos numeros y retorna su suma mas un porcentaje

Body de la llamada:

    {
    "numberOne": 1,
    "numberTwo": 20
    }

Respuesta de ejemplo:

    40

# /calculator/historial
Retorna el historial de llmadas del servicio, de manera paginada. Cada pagina
contiene hasta 20 resultados

Body de la llamada:

    {
		"currentPage":1
    }

Respuesta de ejemplo:

    [
	{
		"id": 1,
		"value1": 1,
		"value2": 20,
		"percentage": 32,
		"result": 27,
		"url": "/calculator/sum"
	},
	{
		"id": 2,
		"value1": 1,
		"value2": 20,
		"percentage": 71,
		"result": 35,
		"url": "/calculator/sum"
	},
	{
		"id": 52,
		"value1": 1,
		"value2": 20,
		"percentage": 4,
		"result": 21,
		"url": "/calculator/sum"
	},
	{
		"id": 102,
		"value1": 1,
		"value2": 20,
		"percentage": 44,
		"result": 30,
		"url": "/calculator/sum"
	},
	{
		"id": 152,
		"value1": 1,
		"value2": 20,
		"percentage": 47,
		"result": 30,
		"url": "/calculator/sum"
	},
	{
		"id": 202,
		"value1": 1,
		"value2": 20,
		"percentage": 20,
		"result": 25,
		"url": "/calculator/sum"
	},
	{
		"id": 252,
		"value1": 1,
		"value2": 20,
		"percentage": 92,
		"result": 40,
		"url": "/calculator/sum"
	},
	{
		"id": 302,
		"value1": 1,
		"value2": 20,
		"percentage": 52,
		"result": 31,
		"url": "/calculator/sum"
	},
	{
		"id": 352,
		"value1": 1,
		"value2": 20,
		"percentage": 48,
		"result": 31,
		"url": "/calculator/sum"
	},
	{
		"id": 402,
		"value1": 1,
		"value2": 20,
		"percentage": 76,
		"result": 36,
		"url": "/calculator/sum"
	},
	{
		"id": 452,
		"value1": 1,
		"value2": 20,
		"percentage": 36,
		"result": 28,
		"url": "/calculator/sum"
	},
	{
		"id": 502,
		"value1": 1,
		"value2": 20,
		"percentage": 91,
		"result": 40,
		"url": "/calculator/sum"
	}
    ]

# Requerimientos


 Debes desarrollar una API REST en Spring Boot utilizando java 11 o superior, con las siguientes funcionalidades:

 1) Debe contener un servicio llamado por api-rest que reciba 2 números, los sume, y le aplique una suba de un porcentaje que
 debe ser adquirido de un servicio externo (por ejemplo, si el servicio recibe 5 y 5 como valores, y el porcentaje devuelto por
 el servicio externo es 10,
 entonces (5 + 5) + 10% = 11). Se deben tener en cuenta las siguientes consideraciones:

      El servicio externo puede ser un mock, tiene que devolver el % sumado.
      Dado que ese % varía poco, podemos considerar que el valor que devuelve ese servicio no va cambiar por 30 minutos.

      Si el servicio externo falla, se debe devolver el último valor retornado. Si no hay valor, debe retornar un error la api.

      Si el servicio falla, se puede reintentar hasta 3 veces.
 2) Historial de todos los llamados a todos los endpoint junto con la respuesta en caso de haber sido exitoso.
      Responder en Json, con data paginada.
      El guardado del historial de llamadas no debe sumar tiempo al servicio invocado,
      y en caso de falla, no debe impactar el llamado al servicio principal.

 3) La api soporta recibir como máximo 3 rpm (request / minuto), en caso de superar ese umbral, debe retornar un error con el código http y mensaje adecuado.

 4) El historial se debe almacenar en una database PostgreSQL.

 5) Incluir errores http. Mensajes y descripciones para la serie 4XX.

