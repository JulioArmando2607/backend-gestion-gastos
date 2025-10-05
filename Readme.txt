com.gestion.gastos
│
├── controller      --> expone los endpoints REST
├── service         --> contiene la lógica de negocio
├── repository      --> interfaces JPA para acceso a datos
├── model
│   ├── entity      --> clases @Entity (Usuario, Movimiento, Categoría)
│   └── dto         --> (opcional) clases para transferencia de datos
├── config          --> configuración de CORS, seguridad, etc.
└── exception       --> manejo centralizado de errores (opcional)

src/
└── main/
    ├── java/
    │   └── com/
    │       └── gestion/
    │           └── gastos/
    │               ├── GestionGastosApplication.java
    │               ├── controller/
    │               ├── service/
    │               ├── repository/
    │               ├── model/
    │               │   ├── entity/
    │               │   └── dto/          ← opcional
    │               ├── config/           ← opcional
    │               └── exception/        ← opcional
    └── resources/
