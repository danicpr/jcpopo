#set page(
  paper: "us-letter",
  margin: (top: 2.2cm, bottom: 2.2cm, left: 2.3cm, right: 2.3cm),
  header: context {
    if counter(page).get().first() > 1 [
      #set text(size: 8.5pt, fill: luma(100))
      #grid(
        columns: (1fr, 1fr),
        align(left)[Universidad de Cartagena — Estructura de Datos],
        align(right)[Taller Tablas Hash y Árbol Binario]
      )
      #v(-4pt)
      #line(length: 100%, stroke: 0.4pt + luma(180))
    ]
  },
  footer: context [
    #set text(size: 8.5pt, fill: luma(100))
    #line(length: 100%, stroke: 0.4pt + luma(180))
    #v(2pt)
    #grid(
      columns: (1fr, 1fr),
      align(left)[Programa Profesional en Ingeniería de Sistemas],
      align(right)[Página #counter(page).display("1 de 1", both: true)]
    )
  ]
)

#set text(font: "Liberation Sans", size: 10pt, lang: "es")
#set par(justify: true, leading: 0.62em)
#set heading(numbering: "1.1.")

#align(center)[
  #v(0.2cm)
  #text(size: 13pt, weight: "bold")[UNIVERSIDAD DE CARTAGENA] \
  #v(2pt)
  #text(size: 11pt, weight: "bold")[FACULTAD DE INGENIERÍA — PROGRAMA DE INGENIERÍA DE SISTEMAS] \
  #v(2pt)
  #text(size: 10.5pt)[Asignatura: Estructura de Datos — Docente: Juan C. García-Ojeda, Ing. MSc., PhD.]
  #v(0.25cm)
  #line(length: 100%, stroke: 1.2pt + black)
  #v(0.15cm)
  #text(size: 15pt, weight: "bold")[SOLUCIÓN TALLER: TABLAS HASH Y ÁRBOL BINARIO]
  #v(0.15cm)
  #line(length: 100%, stroke: 1.2pt + black)
  #v(0.2cm)
]

= Fundamentación Matemática: Reducción ASCII (Radix-2) y Funciones Hash

Cada registro del archivo `MOCK_DATA.csv` (`plate;first_name;last_name;gender`) se identifica por su placa (`plate`). Para transformar un identificador de longitud $L$ con caracteres $c_0, c_1, dots, c_(L-1)$ en la llave numérica $k$ mediante notación *radix-2* (reducción ASCII), se evalúa el polinomio posicional en base $2$:

$ k = sum_(i=0)^(L-1) "ASCII"(c_i) dot 2^((L - 1) - i) $

== Verificación con el registro del enunciado (`05-9165610`)

Para el primer registro de `MOCK_DATA.csv` (`05-9165610;Letitia;Maymand;Female`), la cadena `"05-9165610"` tiene $L = 10$ caracteres, cuyos códigos ASCII son `[48, 53, 45, 57, 49, 54, 53, 54, 49, 48]`:

$ k &= (48 dot 2^9) + (53 dot 2^8) + (45 dot 2^7) + (57 dot 2^6) + (49 dot 2^5) + (54 dot 2^4) + (53 dot 2^3) + (54 dot 2^2) + (49 dot 2^1) + (48 dot 2^0) \
    &= 24576 + 13568 + 5760 + 3648 + 1568 + 864 + 424 + 216 + 98 + 48 = bold(50770) $

Con $m = 256$ slots y $A = (sqrt(5) - 1) / 2 approx 0.6180339887498949$:

1. *Método 1 (Multiplicación)*: $h_1(k) = floor(m dot (k A mod 1))$
   $ 50770 dot A = 31377.58560883... quad => quad (50770 dot A) mod 1 = 0.58560883... $
   $ h_1(50770) = floor(256 dot 0.58560883...) = floor(149.91586...) = bold(149) $

2. *Método 2 (División)*: $h_2(k) = k mod m$
   $ h_2(50770) = 50770 mod 256 = 50770 - (198 dot 256) = 50770 - 50688 = bold(82) $

Ambos resultados coinciden exactamente con los valores especificados en el enunciado del taller ($k = 50770$, $h_1(k) = 149$, $h_2(k) = 82$).

= Estructuras de Datos Empleadas y Estadísticas sobre `MOCK_DATA.csv`

El proyecto Java (`pom.xml` y `nbactions.xml`, compatible con Java 1.8+ y Apache NetBeans 12.3+) utiliza las estructuras de datos del repositorio del docente (`https://github.com/j-c-garciao/academics/tree/master/DataStructures/`):

- *Paquete `co.edu.unicartagena.list` (`Nodo<T>` y `Lista<T>`)*: implementación de la *Session 8* (`primerNodo`, `tamaño`, `adicionar`, `adicionarFinal`, `eliminar`, `imprimir`), con los métodos directos `buscar(T dato)` y `eliminarDato(T dato)` que recorren los enlaces `sig`.
- *Paquete `co.edu.unicartagena.binarytree` (`Node` y `BinaryTree`)*: implementación del Árbol Binario de Búsqueda ordenada por el atributo `long datos` ($k$), donde cada `Node` almacena la referencia `Object info` (`Registro`) y los punteros `izquierdo` y `derecho`. Incluye `insertar`, `buscar` y `eliminar` (manejando los 3 casos clásicos: nodo hoja, nodo con 1 hijo y nodo con 2 hijos reemplazado por el sucesor *in-order*).
- *Paquete `co.edu.unicartagena.hash.model`*: `FuncionHash.java`, `Registro.java`, `TablaHashLista.java`, `TablaHashArbol.java` y `BenchmarkHash.java`.

#table(
  columns: (2.2fr, 1.3fr, 1.3fr),
  inset: 6pt,
  align: (left, center, center),
  stroke: 0.6pt + black,
  [*Métrica sobre `MOCK_DATA.csv` ($n = 1000, m = 256$)*], [*Método 1: Multiplicación*], [*Método 2: División*],
  [Factor de carga ($alpha = n / m$)], [$3.91$ reg./slot], [$3.91$ reg./slot],
  [Slots ocupados ($|T[i]| >= 1$)], [$256 / 256$ ($100.0\%$)], [$251 / 256$ ($98.05\%$)],
  [Slots con colisión ($|T[i]| > 1$)], [$242$ slots], [$230$ slots],
  [Colisiones registradas ($sum_(|T[i]|>1) (|T[i]| - 1)$)], [*744*], [*749*],
  [Longitud máxima en un mismo slot], [$11$ registros], [$10$ registros],
  [Ubicación de `05-9165610` ($k = 50770$)], [Slot `149` ($3$ registros)], [Slot `82` ($5$ registros)],
)

#pagebreak()

= Interfaz Gráfica (Parte 1 y Parte 2)

La interfaz gráfica (`VentanaPrincipal.java` y `PanelVisualizacionSlot.java`) sigue el diseño monocromático en blanco y negro de la Figura 1 del enunciado, con los bloques *File* (`Open`, `Save`, `Close`), *Operations* (`Insert`, `Delete`, `Search`), el recuadro *Collisions*, el selector de función hash y estructura (*Lista Enlazada Simple* o *Árbol Binario*), y el contenedor *T* con los slots que almacenan más de un registro.

#grid(
  columns: (1fr, 1fr),
  gutter: 10pt,
  figure(
    image("gui_metodo1_lista.png", width: 100%),
    caption: [Método 1 (Multiplicación) + Lista Enlazada (`T[149]`, 744 colisiones).]
  ),
  figure(
    image("gui_metodo2_lista.png", width: 100%),
    caption: [Método 2 (División) + Lista Enlazada (`T[82]`, 749 colisiones).]
  )
)

#v(4pt)

#grid(
  columns: (1fr, 1fr),
  gutter: 10pt,
  figure(
    image("gui_metodo1_arbol.png", width: 100%),
    caption: [Parte 2: Método 1 (Multiplicación) + Árbol Binario (`T[149]`).]
  ),
  figure(
    image("gui_metodo2_arbol.png", width: 100%),
    caption: [Parte 2: Método 2 (División) + Árbol Binario (`T[82]`).]
  )
)

= Parte 2: Respuesta al Interrogante (Tabla Hash + Árbol Binario)

*Pregunta:* _«Ajuste el ejercicio anterior de manera tal que la información sea almacenada en cada slot mediante árboles binarios; y responda la siguiente interrogante: en promedio, ¿porcentualmente cuánto mejora el tiempo de respuesta de las acciones, insertar y borrar información en la tabla hash?»_

== Análisis Teórico y Experimental sobre `MOCK_DATA.csv`

1. *Fundamento algorítmico*: En la Tabla Hash con *Lista Enlazada Simple* (`TablaHashLista`), verificar duplicados al insertar al final y localizar un registro por ID para borrarlo requiere recorrer secuencialmente los nodos del slot comparando cadenas (`String.equalsIgnoreCase`) con costo $O(n_"slot")$. En la Tabla Hash con *Árbol Binario de Búsqueda* (`TablaHashArbol`), los nodos dentro de cada slot se ordenan por su llave numérica $k$ (`long datos`). Así, el descenso por las ramas izquierda (`valor < actual.datos`) o derecha (`valor > actual.datos`) se realiza mediante comparaciones enteras en tiempo promedio $O(log_2 n_"slot")$, reduciendo los nodos visitados y evitando comparar cadenas en los nodos intermedios.
2. *Resultados experimentales (`BenchmarkHash.java`, 200 repeticiones sobre los 1000 registros de `MOCK_DATA.csv`)*:

#table(
  columns: (2fr, 1.2fr, 1.2fr, 1.2fr),
  inset: 6pt,
  align: (left, center, center, center),
  stroke: 0.6pt + black,
  [*Operación Evaluada ($n = 1000$ registros)*], [*Lista Enlazada*], [*Árbol Binario*], [*Mejora Porcentual*],
  [Insertar — Método 1 (Multiplicación)], [$0.052$ ms], [$0.042$ ms], [*$+18.20\%$*],
  [Borrar — Método 1 (Multiplicación)], [$0.055$ ms], [$0.046$ ms], [*$+16.42\%$*],
  [*Promedio Método 1 (Multiplicación)*], [$0.054$ ms], [$0.044$ ms], [*$+17.31\%$*],
  [Insertar — Método 2 (División)], [$0.037$ ms], [$0.027$ ms], [*$+25.82\%$*],
  [Borrar — Método 2 (División)], [$0.037$ ms], [$0.030$ ms], [*$+18.64\%$*],
  [*Promedio Método 2 (División)*], [$0.037$ ms], [$0.029$ ms], [*$+22.23\%$*],
)

*Conclusión:* Al almacenar la información de cada slot mediante un *Árbol Binario de Búsqueda* en lugar de una *Lista Enlazada Simple*, el tiempo de respuesta sobre `MOCK_DATA.csv` mejora en promedio un *$18.20\%$ a $25.82\%$ en la acción de insertar* y un *$16.42\%$ a $18.64\%$ en la acción de borrar*, para una *mejora porcentual promedio global de entre $17.31\%$ (Multiplicación) y $22.23\%$ (División)*.