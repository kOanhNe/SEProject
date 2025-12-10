# 📊 SEQUENCE DIAGRAM TRƯỚC & SAU KHI FIX N+1 QUERIES

## 🔴 TRƯỚC (Có N+1 Problem)

### **DIAGRAM 1: View Product List - TRƯỚC**
```
autonumber

actor "User" as user
participant ":ShoesController" as controller
participant ":ShoesService" as service
participant ":ShoesRepository" as repo
participant ":ShoesVariantRepository" as variantRepo
database "Database" as db

title Sequence Diagram: View Product List (BEFORE - N+1 Issue)

user -> controller: GET / (page, size)
activate controller
    controller -> service: getShoesList(page, size)
    activate service
        service -> repo: findAll(pageable)
        activate repo
            repo -> db: SELECT ... JOIN FETCH images
            db --> repo: Page<Shoes> (12 products)
        deactivate repo
        
        loop FOR EACH Shoes (12 times) ❌ PROBLEM
            service -> service: convertToSummaryDto(shoes)
            activate service
                service -> service: getThumbnailUrl()
                service -> service: isOutOfStock(id)
                activate service
                    service -> variantRepo: getTotalStockByShoeId(id) ❌ QUERY 1
                    activate variantRepo
                        variantRepo -> db: SELECT SUM(stock) FROM shoes_variant WHERE shoes_id = 1
                        db --> variantRepo: 150
                    deactivate variantRepo
                    
                    service -> variantRepo: getTotalStockByShoeId(id) ❌ QUERY 2
                    activate variantRepo
                        variantRepo -> db: SELECT SUM(stock) FROM shoes_variant WHERE shoes_id = 2
                        db --> variantRepo: 200
                    deactivate variantRepo
                    
                    ' ... 10 more queries ...
                    
                    service -> variantRepo: getTotalStockByShoeId(id) ❌ QUERY 12
                    activate variantRepo
                        variantRepo -> db: SELECT SUM(stock) FROM shoes_variant WHERE shoes_id = 12
                        db --> variantRepo: 180
                    deactivate variantRepo
                deactivate service
            deactivate service
        end
        
        ' Total: 1 findAll() + 12 getTotalStockByShoeId() = 13 queries
        note right of service
            TOTAL QUERIES: 1 (findAll) + 12 (stock) = 13 DB hits 🔴
            Performance: Chậm khi product nhiều
        end note

    service --> controller: ShoesListDto
    deactivate service
    
    controller -> controller: model.addAttribute()
    controller --> user: Return "shoes-list"
deactivate controller
```

**Số Query: 13** (1 + 12) = ❌ **BAD**

---

### **DIAGRAM 2: View Product Detail - TRƯỚC**
```
autonumber

actor "User" as user
participant ":ShoesController" as controller
participant ":ShoesService" as service
participant ":ShoesRepository" as repo
database "Database" as db

title Sequence Diagram: View Product Detail (BEFORE)

user -> controller: GET /product/{id}
activate controller
    controller -> service: getShoesDetail(id)
    activate service
        service -> repo: findByIdWithDetails(id)
        activate repo
            repo -> db: SELECT ... JOIN FETCH variants/images
            db --> repo: Shoes entity
        deactivate repo
        
        service -> service: convertToDetailDto(shoes)
        
        ' Calculate stock từ loaded variants (OK, no extra query)
        service -> service: Calculate totalStock từ variants set
        
        ' Get related products
        service -> service: getRelatedProducts(shoes)
        activate service
            service -> repo: findRelatedProducts(catId, shoeId)
            activate repo
                repo -> db: SELECT ... for 5 related products
                db --> repo: Page<Shoes> (5 products)
            deactivate repo
            
            ' Convert each related product
            loop FOR EACH related (5 times) ❌ PROBLEM
                service -> service: convertToSummaryDto(related)
                service -> service: isOutOfStock(id) - triggers getTotalStockByShoeId() ❌ QUERY
            end
            
            note right of service
                5 getTotalStockByShoeId() queries cho related products 🔴
            end note
        deactivate service
        
        note right of service
            TOTAL QUERIES: 1 (findByIdWithDetails) + 5 (related stock) = 6 DB hits 🔴
        end note

    service --> controller: ShoesDetailDto
    deactivate service
    
    controller -> controller: model.addAttribute()
    controller --> user: Return "shoes-detail"
deactivate controller
```

**Số Query: 6** (1 + 5) = ❌ **BAD**

---

## 🟢 SAU (Fix N+1 Queries)

### **DIAGRAM 1: View Product List - SAU (FIXED)**
```
autonumber

actor "User" as user
participant ":ShoesController" as controller
participant ":ShoesService" as service
participant ":ShoesRepository" as repo
database "Database" as db

title Sequence Diagram: View Product List (AFTER - Optimized)

user -> controller: GET / (page, size)
activate controller
    controller -> service: getShoesList(page, size)
    activate service
        
        ' ✅ STEP 1: Get all products with one query
        service -> repo: findAll(pageable)
        activate repo
            repo -> db: SELECT ... JOIN FETCH images
            db --> repo: Page<Shoes> (12 products)
        deactivate repo
        
        ' ✅ STEP 2: Get ALL stock data in ONE query
        service -> repo: getAllStocksByIds(List<Long> ids)
        activate repo
            repo -> db: SELECT shoes_id, SUM(stock) FROM shoes_variant WHERE shoes_id IN (1,2,...,12) GROUP BY shoes_id
            db --> repo: Map<Long, Integer> {1→150, 2→200, ..., 12→180}
        deactivate repo
        
        note right of service
            ✅ BATCH LOAD: 1 query gets stock for ALL products!
        end note
        
        ' ✅ STEP 3: Convert with cached stock data
        loop FOR EACH Shoes (12 times) ✅ NO DB QUERY
            service -> service: convertToSummaryDto(shoes)
            activate service
                service -> service: getThumbnailUrl()
                service -> service: isOutOfStock(id) - uses Map<Long, Integer> ✅ NO DB CALL
            deactivate service
        end
        
        note right of service
            ✅ TOTAL QUERIES: 1 (findAll) + 1 (getAllStocks) = 2 DB hits 🟢
            Performance: FAST! 6x faster than before (13 → 2 queries)
        end note

    service --> controller: ShoesListDto
    deactivate service
    
    controller -> controller: model.addAttribute()
    controller --> user: Return "shoes-list"
deactivate controller
```

**Số Query: 2** (1 + 1) = ✅ **GOOD** (giảm 85% queries!)

---

### **DIAGRAM 2: View Product Detail - SAU (FIXED)**
```
autonumber

actor "User" as user
participant ":ShoesController" as controller
participant ":ShoesService" as service
participant ":ShoesRepository" as repo
database "Database" as db

title Sequence Diagram: View Product Detail (AFTER - Optimized)

user -> controller: GET /product/{id}
activate controller
    controller -> service: getShoesDetail(id)
    activate service
        
        ' ✅ Step 1: Get main product with all relations
        service -> repo: findByIdWithDetails(id)
        activate repo
            repo -> db: SELECT ... JOIN FETCH variants/images
            db --> repo: Shoes entity with variants & images
        deactivate repo
        
        service -> service: convertToDetailDto(shoes)
        
        ' ✅ Step 2: Calculate totalStock from LOADED variants (NO extra query)
        service -> service: Calculate totalStock from variants set (In-memory)
        
        ' ✅ Step 3: Get related products
        service -> service: getRelatedProducts(shoes)
        activate service
            ' STEP 3A: Get related products
            service -> repo: findRelatedProducts(catId, shoeId)
            activate repo
                repo -> db: SELECT ... for 5 related products
                db --> repo: Page<Shoes> (5 products with images)
            deactivate repo
            
            ' STEP 3B: ✅ Get stock for all related in ONE query
            service -> repo: getAllStocksByIds(List<Long> relatedIds)
            activate repo
                repo -> db: SELECT shoes_id, SUM(stock) FROM shoes_variant WHERE shoes_id IN (3,5,7,9,11) GROUP BY shoes_id
                db --> repo: Map<Long, Integer>
            deactivate repo
            
            note right of service
                ✅ BATCH LOAD: 1 query gets stock for ALL 5 related products!
            end note
            
            ' STEP 3C: Convert related with cached stock
            loop FOR EACH related (5 times) ✅ NO DB QUERY
                service -> service: convertToSummaryDto(related)
                service -> service: isOutOfStock(id) - uses Map ✅ NO DB CALL
            end
        deactivate service
        
        note right of service
            ✅ TOTAL QUERIES: 1 (main) + 1 (related) + 1 (related stock) = 3 DB hits 🟢
            Performance: 2x faster than before (6 → 3 queries)
        end note

    service --> controller: ShoesDetailDto
    deactivate service
    
    controller -> controller: model.addAttribute()
    controller --> user: Return "shoes-detail"
deactivate controller
```

**Số Query: 3** (1 + 1 + 1) = ✅ **GOOD** (giảm 50% queries!)

---

## 📊 SO SÁNH TRƯỚC SAU

### **View Product List**

| Metric | TRƯỚC ❌ | SAU ✅ | Cải thiện |
|--------|---------|--------|----------|
| **Tổng Queries** | 13 | 2 | **6.5x giảm** |
| **Database Hits** | 12 + 1 | 1 + 1 | **85% giảm** |
| **Loop Iterations** | 12 with DB calls | 12 in-memory | **No DB I/O** |
| **Performance** | Chậm | Rất nhanh | **⚡ 6x faster** |

### **View Product Detail**

| Metric | TRƯỚC ❌ | SAU ✅ | Cải thiện |
|--------|---------|--------|----------|
| **Tổng Queries** | 6 | 3 | **2x giảm** |
| **Related Product Queries** | 5 separate | 1 batch | **80% giảm** |
| **Related Conversion** | 5 with DB calls | 5 in-memory | **No DB I/O** |
| **Performance** | Chậm | Nhanh | **⚡ 2x faster** |

---

## 🔄 CÁC THAY ĐỔI TRONG SEQUENCE DIAGRAM

### **Key Differences:**

#### **1. TRƯỚC (Bad Pattern):**
```
loop for each product:
    call isOutOfStock(id)
        → call getTotalStockByShoeId(id)  ❌ QUERY inside loop
```

#### **SAU (Good Pattern):**
```
getAllStocksByIds(allIds)  ✅ ONE query for all
    → returns Map<Long, Integer>

loop for each product:
    call isOutOfStock(id)
        → lookup Map (NO query)  ✅ In-memory lookup
```

---

## ✅ SEQUENCE DIAGRAM CHANGES SUMMARY

### **Thay đổi chính:**

| Thành phần | TRƯỚC | SAU |
|-----------|-------|-----|
| **findAll query** | ✅ 1 query | ✅ 1 query (same) |
| **Stock queries** | ❌ N queries (1 per product) | ✅ 1 batch query |
| **Loop logic** | ❌ DB calls inside loop | ✅ In-memory lookup |
| **Related queries** | ❌ N queries (1 per related) | ✅ 1 batch query |
| **Data structure** | ❌ Query for each | ✅ Map lookup |
| **Network roundtrips** | ❌ 13-6 roundtrips | ✅ 2-3 roundtrips |

---

## 🎯 VISUAL COMPARISON

### **TRƯỚC - Sequence Flow (Slow):**
```
Request → findAll() ──┐
                       ├→ getStock(1) ──→ DB ──→ isOutOfStock ✅
                       ├→ getStock(2) ──→ DB ──→ isOutOfStock ✅
                       ├→ getStock(3) ──→ DB ──→ isOutOfStock ✅
                       ├→ ...
                       └→ getStock(12) ─→ DB ──→ isOutOfStock ✅

Total: 1 + 12 = 13 database roundtrips
```

### **SAU - Sequence Flow (Fast):**
```
Request → findAll() ──┐
           getAllStocks() ──→ DB [all stocks at once]
                       └→ getStock(1) ──→ Map lookup ✅
                       ├→ getStock(2) ──→ Map lookup ✅
                       ├→ getStock(3) ──→ Map lookup ✅
                       ├→ ...
                       └→ getStock(12) ─→ Map lookup ✅

Total: 1 + 1 = 2 database roundtrips (+ 11 memory lookups)
```

---

## 📝 CONCLUSION

### **Sequence Diagram Changes:**

1. **Thêm 1 bước mới:** `getAllStocksByIds()` batch query
2. **Bỏ vòng lặp query:** Thay `getTotalStockByShoeId()` bằng Map lookup
3. **Giảm N+1 queries:** 
   - List page: 13 → 2 queries (85% reduction)
   - Detail page: 6 → 3 queries (50% reduction)

### **Diagram Structure Thay Đổi:**
- ✅ **Fewer database interactions**
- ✅ **Batch operations instead of loops**
- ✅ **In-memory lookups for repeated data**
- ✅ **Much faster performance**

### **Tóm tắt:**
**SAU KHI FIX, SEQUENCE DIAGRAM CÓ THAY ĐỔI SẮC NÉT:**
- Không còn loop with DB calls
- Thay vào đó là batch query trước → in-memory lookup trong loop
- Query count giảm 50-85%
- Performance tăng 2-6x
