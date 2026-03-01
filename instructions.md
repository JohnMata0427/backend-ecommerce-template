# Backend E-Commerce Template — Instrucciones para Frontend

## 1. Visión General

API REST para e-commerce construida con **Spring Boot 4.0.3** y **Java 25**.  
Base de datos **PostgreSQL**, caché con **Redis**, imágenes en **Cloudinary**.  
Seguridad con **Spring Security** (HTTP Basic). Hilos virtuales habilitados (Project Loom).

**Base URL:** `http://localhost:8080/api/v1`

---

## 2. Stack Tecnológico

| Componente | Tecnología |
|---|---|
| Framework | Spring Boot 4.0.3 |
| Java | 25 (Virtual Threads habilitados) |
| Base de datos | PostgreSQL |
| Caché | Redis (TTL: 10 min) |
| ORM | Hibernate / JPA (ddl-auto: validate) |
| Mapeo DTO ↔ Entity | MapStruct 1.6.3 |
| Imágenes | Cloudinary |
| Resiliencia | Resilience4j (Circuit Breaker, Retry, TimeLimiter) |
| Métricas | Actuator + Prometheus + Micrometer |
| Build | Maven |
| Contenedor | Docker (multi-stage, eclipse-temurin:25) |

---

## 3. Autenticación y Seguridad

- **Método:** HTTP Basic Authentication
- **Endpoints GET** → públicos (no requieren autenticación)
- **Endpoints POST, PUT, DELETE** → requieren autenticación
- CSRF deshabilitado
- CORS habilitado con defaults
- Sesión: STATELESS

**Headers requeridos para mutaciones:**

```
Authorization: Basic <base64(usuario:contraseña)>
```

> Las credenciales se configuran via variables de entorno `SECURITY_USER` y `SECURITY_PASSWORD`.

---

## 4. Modelo de Datos

### 4.1 BaseEntity (campos heredados por todas las entidades)

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `UUID` | Identificador único, generado automáticamente |
| `createdAt` | `Instant` (ISO 8601) | Fecha de creación (auto) |
| `updatedAt` | `Instant` (ISO 8601) | Fecha de última modificación (auto) |

### 4.2 Category

| Campo | Tipo | Restricciones |
|---|---|---|
| `name` | `string` | Obligatorio, único, máx 150 chars |
| `description` | `string` | Opcional, máx 5000 chars |
| `active` | `boolean` | Default `true` |
| *subcategories* | relación 1:N | — |

### 4.3 Subcategory

| Campo | Tipo | Restricciones |
|---|---|---|
| `name` | `string` | Obligatorio, máx 150 chars. Único por categoría (`name` + `category_id`) |
| `description` | `string` | Opcional, máx 5000 chars |
| `categoryId` | `UUID` | Obligatorio (FK → Category) |
| `active` | `boolean` | Default `true` |
| *products* | relación 1:N | — |

### 4.4 Brand

| Campo | Tipo | Restricciones |
|---|---|---|
| `name` | `string` | Obligatorio, único, máx 150 chars |
| `description` | `string` | Opcional, máx 5000 chars |
| `active` | `boolean` | Default `true` |
| *products* | relación 1:N | — |

### 4.5 Supplier

| Campo | Tipo | Restricciones |
|---|---|---|
| `name` | `string` | Obligatorio, máx 255 chars |
| `contactName` | `string` | Opcional, máx 255 chars |
| `email` | `string` | Obligatorio, único, email válido, máx 255 chars |
| `phone` | `string` | Opcional, máx 50 chars |
| `address` | `string` | Opcional, máx 5000 chars |
| `active` | `boolean` | Default `true` |
| *products* | relación 1:N | — |

### 4.6 Product

| Campo | Tipo | Restricciones |
|---|---|---|
| `name` | `string` | Obligatorio, máx 255 chars |
| `description` | `string` | Opcional, máx 5000 chars |
| `price` | `BigDecimal` | Obligatorio, > 0.01, precisión 12 escala 2 |
| `stock` | `integer` | Obligatorio, ≥ 0 |
| `sku` | `string` | Obligatorio, único, máx 100 chars |
| `subcategoryId` | `UUID` | Opcional (FK → Subcategory) |
| `supplierId` | `UUID` | Opcional (FK → Supplier) |
| `brandId` | `UUID` | Opcional (FK → Brand) |
| `imageUrl` | `string` | URL de Cloudinary (gestionado por backend) |
| `imagePublicId` | `string` | ID público de Cloudinary (gestionado por backend) |
| `active` | `boolean` | Default `true` |

### Diagrama de Relaciones

```
Category 1 ──── N Subcategory 1 ──── N Product N ──── 1 Brand
                                       Product N ──── 1 Supplier
```

---

## 5. Endpoints de la API

### 5.1 Categories — `/api/v1/categories`

| Método | Ruta | Descripción | Auth |
|---|---|---|---|
| `POST` | `/` | Crear categoría | ✅ |
| `GET` | `/` | Listar todas (paginado) | ❌ |
| `GET` | `/{id}` | Obtener por ID | ❌ |
| `GET` | `/active` | Listar activas (paginado) | ❌ |
| `GET` | `/search?name={name}` | Buscar por nombre (parcial, case-insensitive) | ❌ |
| `PUT` | `/{id}` | Actualizar categoría | ✅ |
| `DELETE` | `/{id}` | Eliminar categoría | ✅ |

**Request Body (POST/PUT):**
```json
{
  "name": "Electrónica",
  "description": "Dispositivos electrónicos y accesorios",
  "active": true
}
```

**Response Body:**
```json
{
  "id": "736eed2b-7f9f-4b68-8459-1315e5fd6cff",
  "name": "Electrónica",
  "description": "Dispositivos electrónicos y accesorios",
  "active": true,
  "subcategoryCount": 3,
  "createdAt": "2025-01-15T10:30:00Z",
  "updatedAt": "2025-01-15T10:30:00Z"
}
```

---

### 5.2 Subcategories — `/api/v1/subcategories`

| Método | Ruta | Descripción | Auth |
|---|---|---|---|
| `POST` | `/` | Crear subcategoría | ✅ |
| `GET` | `/` | Listar todas (paginado) | ❌ |
| `GET` | `/{id}` | Obtener por ID | ❌ |
| `GET` | `/active` | Listar activas (paginado) | ❌ |
| `GET` | `/category/{categoryId}` | Listar por categoría (paginado) | ❌ |
| `GET` | `/search?name={name}` | Buscar por nombre | ❌ |
| `PUT` | `/{id}` | Actualizar subcategoría | ✅ |
| `DELETE` | `/{id}` | Eliminar subcategoría | ✅ |

**Request Body (POST/PUT):**
```json
{
  "name": "Laptops",
  "description": "Computadoras portátiles y notebooks",
  "categoryId": "736eed2b-7f9f-4b68-8459-1315e5fd6cff",
  "active": true
}
```

**Response Body:**
```json
{
  "id": "6b98f181-07b3-44c2-8dfc-8a89f8157884",
  "name": "Laptops",
  "description": "Computadoras portátiles y notebooks",
  "categoryId": "736eed2b-7f9f-4b68-8459-1315e5fd6cff",
  "categoryName": "Electrónica",
  "active": true,
  "productCount": 5,
  "createdAt": "2025-01-15T10:30:00Z",
  "updatedAt": "2025-01-15T10:30:00Z"
}
```

---

### 5.3 Brands — `/api/v1/brands`

| Método | Ruta | Descripción | Auth |
|---|---|---|---|
| `POST` | `/` | Crear marca | ✅ |
| `GET` | `/` | Listar todas (paginado) | ❌ |
| `GET` | `/{id}` | Obtener por ID | ❌ |
| `GET` | `/active` | Listar activas (paginado) | ❌ |
| `GET` | `/search?name={name}` | Buscar por nombre | ❌ |
| `PUT` | `/{id}` | Actualizar marca | ✅ |
| `DELETE` | `/{id}` | Eliminar marca | ✅ |

**Request Body (POST/PUT):**
```json
{
  "name": "ASUS",
  "description": "Fabricante de hardware y electrónica",
  "active": true
}
```

**Response Body:**
```json
{
  "id": "uuid",
  "name": "ASUS",
  "description": "Fabricante de hardware y electrónica",
  "active": true,
  "productCount": 12,
  "createdAt": "2025-01-15T10:30:00Z",
  "updatedAt": "2025-01-15T10:30:00Z"
}
```

---

### 5.4 Suppliers — `/api/v1/suppliers`

| Método | Ruta | Descripción | Auth |
|---|---|---|---|
| `POST` | `/` | Crear proveedor | ✅ |
| `GET` | `/` | Listar todos (paginado) | ❌ |
| `GET` | `/{id}` | Obtener por ID | ❌ |
| `GET` | `/active` | Listar activos (paginado) | ❌ |
| `GET` | `/search?name={name}` | Buscar por nombre | ❌ |
| `PUT` | `/{id}` | Actualizar proveedor | ✅ |
| `DELETE` | `/{id}` | Eliminar proveedor | ✅ |

**Request Body (POST/PUT):**
```json
{
  "name": "ASUS Technology",
  "contactName": "Juan Pérez",
  "email": "ventas@asus-tech.com",
  "phone": "+57 300 123 4567",
  "address": "Calle 100 #15-20, Bogotá, Colombia",
  "active": true
}
```

**Response Body:**
```json
{
  "id": "uuid",
  "name": "ASUS Technology",
  "contactName": "Juan Pérez",
  "email": "ventas@asus-tech.com",
  "phone": "+57 300 123 4567",
  "address": "Calle 100 #15-20, Bogotá, Colombia",
  "active": true,
  "productCount": 8,
  "createdAt": "2025-01-15T10:30:00Z",
  "updatedAt": "2025-01-15T10:30:00Z"
}
```

---

### 5.5 Products — `/api/v1/products`

| Método | Ruta | Descripción | Auth |
|---|---|---|---|
| `POST` | `/` | Crear producto (`multipart/form-data`) | ✅ |
| `GET` | `/` | Listar todos (paginado) | ❌ |
| `GET` | `/{id}` | Obtener por ID | ❌ |
| `GET` | `/sku/{sku}` | Obtener por SKU | ❌ |
| `GET` | `/active` | Listar activos (paginado) | ❌ |
| `GET` | `/subcategory/{subcategoryId}` | Listar por subcategoría (paginado) | ❌ |
| `GET` | `/supplier/{supplierId}` | Listar por proveedor (paginado) | ❌ |
| `GET` | `/brand/{brandId}` | Listar por marca (paginado) | ❌ |
| `GET` | `/category/{categoryId}` | Listar por categoría (paginado) | ❌ |
| `GET` | `/search?name={name}` | Buscar por nombre | ❌ |
| `PUT` | `/{id}` | Actualizar producto (`multipart/form-data`) | ✅ |
| `DELETE` | `/{id}` | Eliminar producto (y su imagen de Cloudinary) | ✅ |

**⚠️ IMPORTANTE:** Los endpoints POST y PUT de productos usan `multipart/form-data`, NO `application/json`.

**Estructura del multipart:**
- Part `"product"` → JSON con `Content-Type: application/json`
- Part `"image"` → archivo de imagen (opcional)

**JSON del part "product" (POST):**
```json
{
  "name": "Laptop Gaming ASUS",
  "description": "Laptop gaming con RTX 4060 16GB RAM",
  "price": 1299.99,
  "stock": 25,
  "sku": "LAPTOP-ASUS-001",
  "subcategoryId": "6b98f181-07b3-44c2-8dfc-8a89f8157884",
  "supplierId": "b42283b6-e259-44f0-93d9-76c3387c7786",
  "brandId": "uuid-de-la-marca",
  "active": true
}
```

**Ejemplo con FormData en JavaScript:**
```javascript
const formData = new FormData();

const productData = {
  name: "Laptop Gaming ASUS",
  description: "Laptop gaming con RTX 4060",
  price: 1299.99,
  stock: 25,
  sku: "LAPTOP-ASUS-001",
  subcategoryId: "uuid",
  supplierId: "uuid",
  brandId: "uuid",
  active: true
};

formData.append("product", new Blob([JSON.stringify(productData)], { type: "application/json" }));
formData.append("image", fileInput.files[0]); // Opcional

const response = await fetch("http://localhost:8080/api/v1/products", {
  method: "POST",
  headers: {
    "Authorization": "Basic " + btoa("usuario:contraseña")
  },
  body: formData
});
```

**Response Body:**
```json
{
  "id": "b93c4cb6-5281-4d36-8891-432af258265a",
  "name": "Laptop Gaming ASUS",
  "description": "Laptop gaming con RTX 4060 16GB RAM",
  "price": 1299.99,
  "stock": 25,
  "sku": "LAPTOP-ASUS-001",
  "subcategoryId": "6b98f181-07b3-44c2-8dfc-8a89f8157884",
  "subcategoryName": "Laptops",
  "supplierId": "b42283b6-e259-44f0-93d9-76c3387c7786",
  "supplierName": "ASUS Technology",
  "brandId": "uuid-de-la-marca",
  "brandName": "ASUS",
  "imageUrl": "https://res.cloudinary.com/.../products/imagen.webp",
  "imagePublicId": "products/imagen",
  "active": true,
  "createdAt": "2025-01-15T10:30:00Z",
  "updatedAt": "2025-01-15T10:30:00Z"
}
```

---

## 6. Paginación

Todos los endpoints que retornan listas usan **Spring Data Page**. Tamaño por defecto: **20**, máximo: **100**. La paginación es **1-indexed** (la primera página es `page=1`).

**Query params de paginación:**

| Param | Default | Descripción |
|---|---|---|
| `page` | `1` | Número de página (1-indexed) |
| `size` | `20` | Elementos por página (máx 100) |
| `sort` | `name,asc` | Campo y dirección de ordenamiento |

**Ejemplo:** `GET /api/v1/products?page=1&size=10&sort=price,desc`

**Estructura de respuesta paginada:**
```json
{
  "content": [ /* array de elementos */ ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": { "sorted": true, "direction": "ASC", "property": "name" },
    "offset": 0,
    "paged": true
  },
  "totalElements": 150,
  "totalPages": 8,
  "size": 20,
  "number": 0,
  "first": true,
  "last": false,
  "empty": false,
  "numberOfElements": 20
}
```

---

## 7. Manejo de Errores

Las respuestas de error siguen un formato consistente:

**Error estándar:**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Producto no encontrado con id: b93c4cb6-...",
  "path": "/api/v1/products/b93c4cb6-...",
  "timestamp": "2025-01-15T10:30:00Z",
  "validationErrors": null
}
```

**Error de validación (400):**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validación en los campos enviados",
  "path": "/api/v1/products",
  "timestamp": "2025-01-15T10:30:00Z",
  "validationErrors": {
    "name": "El nombre del producto es obligatorio",
    "price": "El precio debe ser mayor a 0",
    "sku": "El SKU es obligatorio"
  }
}
```

### Códigos HTTP utilizados

| Código | Significado | Cuándo |
|---|---|---|
| `200` | OK | GET exitoso, PUT exitoso |
| `201` | Created | POST exitoso |
| `204` | No Content | DELETE exitoso |
| `400` | Bad Request | Validación fallida |
| `401` | Unauthorized | Sin credenciales o inválidas |
| `404` | Not Found | Recurso no encontrado |
| `409` | Conflict | Duplicado (ej: SKU, nombre de categoría, email) |
| `500` | Internal Server Error | Error no controlado |
| `502` | Bad Gateway | Error con Cloudinary |

---

## 8. Validaciones por Entidad

### Category / Brand
- `name` → obligatorio, máx 150 chars, único (case-insensitive)
- `description` → opcional, máx 5000 chars
- `active` → opcional (default `true`)

### Subcategory
- `name` → obligatorio, máx 150 chars, único dentro de su categoría
- `description` → opcional, máx 5000 chars
- `categoryId` → obligatorio
- `active` → opcional (default `true`)

### Supplier
- `name` → obligatorio, máx 255 chars
- `contactName` → opcional, máx 255 chars
- `email` → obligatorio, email válido, único, máx 255 chars
- `phone` → opcional, máx 50 chars
- `address` → opcional, máx 5000 chars
- `active` → opcional (default `true`)

### Product
- `name` → obligatorio, máx 255 chars
- `description` → opcional, máx 5000 chars
- `price` → obligatorio, > 0.01
- `stock` → obligatorio, ≥ 0
- `sku` → obligatorio, máx 100 chars, único
- `subcategoryId` → opcional
- `supplierId` → opcional
- `brandId` → opcional
- `active` → opcional (default `true`)
- `image` → opcional, máx 5MB por archivo, 10MB total por request

---

## 9. Subida de Imágenes

- Solo aplica a **Products** (POST y PUT)
- Las imágenes se suben directamente a **Cloudinary** en la carpeta `products/`
- El backend devuelve `imageUrl` (URL pública) e `imagePublicId` (ID interno)
- Al actualizar un producto con nueva imagen, la anterior se elimina automáticamente de Cloudinary
- Al eliminar un producto, su imagen se elimina automáticamente de Cloudinary
- Límites: 5MB por archivo, 10MB por request total

---

## 10. Caché (Redis)

- Se cachean las consultas por ID (`getById`) para: products, categories, subcategories, brands, suppliers
- TTL: **10 minutos** (600,000 ms)
- Prefix de claves: `ecommerce:`
- Al crear → se guarda en caché
- Al actualizar → se actualiza en caché (`@CachePut`)
- Al eliminar → se invalida la caché (`@CacheEvict`)
- Las consultas paginadas NO se cachean

---

## 11. Serialización JSON (Jackson)

- Propiedades `null` se **omiten** de la respuesta (`NON_NULL`)
- Propiedades desconocidas en el request se **ignoran**
- Timezone: **UTC**
- Fechas: formato ISO 8601 (`Instant`)

---

## 12. Variables de Entorno Requeridas

| Variable | Descripción |
|---|---|
| `DB_HOST` | Host de PostgreSQL |
| `DB_PORT` | Puerto de PostgreSQL |
| `DB_NAME` | Nombre de la base de datos |
| `DB_USERNAME` | Usuario de PostgreSQL |
| `DB_PASSWORD` | Contraseña de PostgreSQL |
| `REDIS_URL` | URL completa de Redis (ej: `redis://localhost:6379`) |
| `SECURITY_USER` | Usuario para HTTP Basic Auth |
| `SECURITY_PASSWORD` | Contraseña para HTTP Basic Auth |
| `CLOUDINARY_CLOUD_NAME` | Cloud name de Cloudinary |
| `CLOUDINARY_API_KEY` | API key de Cloudinary |
| `CLOUDINARY_API_SECRET` | API secret de Cloudinary |

---

## 13. Endpoints de Monitoreo (Actuator)

| Ruta | Descripción |
|---|---|
| `/actuator/health` | Estado de salud (liveness/readiness) |
| `/actuator/info` | Info de la aplicación |
| `/actuator/metrics` | Métricas de la JVM y la app |
| `/actuator/prometheus` | Métricas en formato Prometheus |
| `/actuator/caches` | Estado de las cachés |
| `/actuator/loggers` | Niveles de logging |

> Todos los endpoints de Actuator son públicos (no requieren auth).

---

## 14. Consideraciones para el Frontend

1. **IDs son UUID** — Todos los identificadores son UUID v4 como strings.
2. **Productos usan multipart** — No enviar JSON directo, usar `FormData` con un Blob JSON para el part `"product"`.
3. **Paginación 1-indexed** — La primera página es `page=1` (no `page=0`).
4. **Errores de validación** — Revisar el campo `validationErrors` del error para mapear errores a campos de formulario.
5. **Búsqueda parcial** — El endpoint `/search?name=` busca coincidencias parciales case-insensitive.
6. **Campos nulos en PUT** — Los campos enviados como `null` en un update se **ignoran** (no sobreescriben; se usa `NullValuePropertyMappingStrategy.IGNORE`).
7. **Respuestas comprimidas** — El servidor habilita gzip para responses > 1KB.
8. **CORS** — Habilitado con defaults de Spring (todos los orígenes permitidos para GET).
9. **Contadores** — Los responses de Category, Subcategory, Brand y Supplier incluyen contadores (`subcategoryCount`, `productCount`) que indican cuántos hijos tienen.
10. **Imágenes** — `imageUrl` en Product es la URL pública para mostrar la imagen; `imagePublicId` es interno y no necesita mostrarse al usuario.
