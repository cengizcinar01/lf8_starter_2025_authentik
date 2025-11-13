package de.szut.lf8_starter.hello;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HelloService {
    private final HelloRepository repository;

    public HelloService(HelloRepository repository) {
        this.repository = repository;
    }

    public HelloEntity create(HelloEntity entity) {
        return this.repository.save(entity);
    }

    @Transactional(readOnly = true)
    public List<HelloEntity> readAll() {
        return this.repository.findAll();
    }

    @Transactional(readOnly = true)
    public HelloEntity readById(long id) {
        Optional<HelloEntity> optionalQualification = this.repository.findById(id);
        if (optionalQualification.isEmpty()) {
            return null;
        }
        return optionalQualification.get();
    }


    public void delete(HelloEntity entity) {
        this.repository.delete(entity);
    }

    @Transactional(readOnly = true)
    public List<HelloEntity> findByMessage(String message) {
        return this.repository.findByMessage(message);
    }
}
