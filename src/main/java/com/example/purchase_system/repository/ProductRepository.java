package com.example.purchase_system.repository;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.purchase_system.entity.Product;
import com.example.purchase_system.util.CsvFileUtil;
import com.example.purchase_system.util.CsvStatusType;
import com.example.purchase_system.util.ModelStatus;


@Repository
public class ProductRepository {
    private Map<Integer, Product> products;
    private int maxId = 0;

    public ProductRepository() {
        products = new LinkedHashMap<>();
        loadProductFromCsv();
    }

    public Product create(String barcode, String name) {
        CsvStatusType type = CsvStatusType.CREATE;
        validCrudParameter(null, barcode, name, type);
        validCrudData(null, barcode, name, type);
        
        int id = this.maxId + 1;
        
        Product product = new Product(id, barcode, name);
        saveProductToCsv(product, type);
        products.put(id, product);
        this.maxId = id;
        return product;
    }

    public Product update(Integer id, String barcode, String name) {
        CsvStatusType type = CsvStatusType.UPDATE;
        validCrudParameter(id, barcode, name, type);
        validCrudData(id, barcode, name, type);

        Product newPdut = new Product(id, barcode, name);
        saveProductToCsv(newPdut, type);
        products.put(id, newPdut);
        return newPdut;
    }

    public Product delete(Integer id) {
        CsvStatusType type = CsvStatusType.DELETE;
        validCrudParameter(id, null, null, type);
        validCrudData(id, null, null, type);

        String lastName = products.get(id).getName();
        String lastBarcode = products.get(id).getBarcode();
        Product delPdut = new Product(id, lastBarcode, "(已刪除商品) " + lastName, ModelStatus.DOWN);
        saveProductToCsv(delPdut, type);
        products.put(id, delPdut);
        return delPdut;
    }

    private void validCrudParameter(Integer id, String barcode, String name, CsvStatusType type) {
        switch(type) {
            case DELETE:
            case UPDATE: 
                if(id == null) {
                    throw new IllegalArgumentException("Id is wrong.");
                }
                if(type == CsvStatusType.DELETE) {
                    break;
                }
            case CREATE:
                if(name == null || name.isBlank()) {
                    throw new IllegalArgumentException("Name cannot be blank.");
                }
                break;
            default: throw new IllegalArgumentException("Wrong csv status, please check.");
        }
    }

    private void validCrudData(Integer id, String barcode, String name, CsvStatusType type) {
        switch(type) {
            case DELETE:
            case UPDATE: 
                if(!findById(id).isPresent()) {
                    throw new IllegalArgumentException("Id is wrong.");
                }
                if(findById(id).get().getStatus() == ModelStatus.DOWN) {
                    throw new IllegalArgumentException("Product cannot be edited.");
                }
                if(type == CsvStatusType.DELETE) {
                    break;
                }
            case CREATE:
                if(findByBarcode(barcode).isPresent()) {
                    if(id == null || id != findByBarcode(barcode).get().getId()){
                        throw new IllegalArgumentException("Barcode already exists.");
                    }
                }
                if(findByName(name).stream()
                                   .filter(n -> id == null || id != n.getId())
                                   .anyMatch(n -> name.equals(n.getName()))) {
                    throw new IllegalArgumentException("Name already exists.");
                }
                break;

            default: throw new IllegalArgumentException("Wrong csv status, please check.");
        }
    }

    private void saveProductToCsv(Product product, CsvStatusType type) {
        Path path = Path.of("data", "productList.csv");
        try {
            String line =  type.getStatus() + "," 
                            + product.getId() + "," 
                            + ((product.getBarcode() == null) ? "" : product.getBarcode()) + "," 
                            + product.getName() + System.lineSeparator();
            Files.writeString(path, line, StandardCharsets.UTF_8
                                , StandardOpenOption.APPEND
            );
        } catch(IOException e) {
            throw new IllegalStateException("Cannot save productList.csv", e);
        }
    }

    private void loadProductFromCsv() {
        try {
            List<String> lines = CsvFileUtil.loadDocument("data"
                                                        , "productList.csv"
                                                        , "STATUS,ID,BARCODE,NAME");
            for(int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if(line.isBlank()){continue;} // 我們決定採用 append 但以防萬一還是保留
                String[] columns = line.split(",", 4);
                if(columns.length < 4) {throw new IllegalArgumentException(
                    "productList.csv format error at line " + (i+1));
                }

                String status = columns[0].trim();
                int id = Integer.parseInt(columns[1].trim());
                String barcode = columns[2].trim();
                    if(barcode.isEmpty()) {barcode = null;}
                String name = columns[3].trim();
                Product product = new Product(id, barcode, name);
                
                if(CsvStatusType.CREATE.getStatus().equals(status)
                    && !products.containsKey(id)
                    && id > maxId) {
                        products.put(id, product);
                        this.maxId = id;
                } else if(CsvStatusType.UPDATE.getStatus().equals(status)
                    && products.containsKey(id)
                    && products.get(id).getStatus().equals(ModelStatus.ACTIVE)) {
                        products.put(id, product);
                } else if(CsvStatusType.DELETE.getStatus().equals(status)
                    && products.containsKey(id)
                    && products.get(id).getStatus().equals(ModelStatus.ACTIVE)) {
                        product.setStatus(ModelStatus.DOWN);
                        products.put(id, product);
                } else {
                    throw new IllegalStateException("productList.csv format error at line " + (i+1));
                }
            }
        } catch(IOException e) {
            throw new IllegalStateException("Cannot read productList.csv", e);
        }
    }

    
    public Optional<Product> findById(int id) {
        return Optional.ofNullable(products.get(id));
    }

    public Optional<Product> findByBarcode(String barcode) {
        if(barcode == null || barcode.isBlank()) return Optional.empty();

        for (Product product : products.values()) {
            if (barcode.equals(product.getBarcode())) {
                return Optional.of(product);
            }
        }
        
        return Optional.empty();
    }
    public List<Product> findByName(String name) {
        List<Product> result = new ArrayList<>();
        
        if (name == null || name.isBlank()) return result;
        
        for (Product product : products.values()) {
            if (product.getName().contains(name)) {
                result.add(product);
            }
        }

        return result;
    }

    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }   

}