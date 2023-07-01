package org.swdc.demo.table.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.swdc.demo.table.entity.StoredColumnType;
import org.swdc.demo.table.repo.StoredColumnTypeRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StoredColumnTypeService {

    @Autowired
    private StoredColumnTypeRepository repository;

    public Set<StoredColumnType> getAllColumnTypes(){
        List<StoredColumnType> types = repository.findAll();
        if (types == null) {
            return Collections.emptySet();
        }
        return types.stream().map(StoredColumnType::unManaged)
                .collect(Collectors.toSet());
    }

    public StoredColumnType addColumnType(String name, String regex, String editorType) {
        StoredColumnType type = repository.getColumnTypeByName(name);
        if (type != null) {
            type.setDescriptor(regex);
            return repository.save(type).unManaged();
        }
        type = new StoredColumnType();
        type.setName(name);
        type.setDescriptor(regex);
        type.setEditorId(editorType);
        type = repository.save(type);
        return type.unManaged();
    }

    public StoredColumnType updateColumnType(Long id, String name, String regex,String editorId) {
        if (id == null || id < 0 || name == null || name.isBlank() || regex == null || regex.isBlank()) {
            return null;
        }
        StoredColumnType type = repository.findById(id).orElse(null);
        if (type == null) {
            return null;
        }
        type.setName(name);
        type.setDescriptor(regex);
        type.setEditorId(editorId);
        type = repository.save(type);
        return type.unManaged();
    }

    public StoredColumnType getColumnType(Long id) {
        if (id == null || id < 0) {
            return null;
        }
        StoredColumnType type = repository.findById(id).orElse(null);
        return type == null ? null : type.unManaged();
    }

    public StoredColumnType deleteColumnType(Long id) {
        if (id == null || id < 0) {
            return null;
        }
        StoredColumnType type = repository.findById(id).orElse(null);
        if (type == null) {
            return null;
        }
        StoredColumnType result = type.unManaged();
        repository.delete(type);
        return result;
    }

}
