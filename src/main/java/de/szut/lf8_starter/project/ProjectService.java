package de.szut.lf8_starter.project;

import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.project.dto.ProjectCreateDto;
import de.szut.lf8_starter.project.dto.ProjectGetDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectGetDto createProject(ProjectCreateDto createDto) {
        if (createDto.getEmployeeIds() == null) {
            createDto.setEmployeeIds(new HashSet<>());
        }

        if (createDto.getStatus() == null) {
            createDto.setStatus(ProjectStatus.PLANNED);
        }

        ProjectEntity entity = projectMapper.mapCreateDtoToEntity(createDto);

        ProjectEntity savedEntity = projectRepository.save(entity);

        return projectMapper.mapEntityToGetDto(savedEntity);
    }

    public List<ProjectGetDto> getAllProjects() {
        List<ProjectEntity> entities = projectRepository.findAll();
        return entities.stream()
                .map(projectMapper::mapEntityToGetDto)
                .toList();
    }

    public ProjectGetDto getProjectById(Long id) {
        Optional<ProjectEntity> entity = projectRepository.findById(id);

        if (entity.isEmpty()) {
            throw new ResourceNotFoundException("Project with id " + id + " not found");
        }

        return projectMapper.mapEntityToGetDto(entity.get());
    }

    public ProjectGetDto updateProject(Long id, ProjectCreateDto updateDto) {
        Optional<ProjectEntity> existingEntity = projectRepository.findById(id);

        if (existingEntity.isEmpty()) {
            throw new ResourceNotFoundException("Project with id " + id + " not found");
        }

        if (updateDto.getEmployeeIds() == null) {
            updateDto.setEmployeeIds(new HashSet<>());
        }

        ProjectEntity entityToUpdate = projectMapper.mapCreateDtoToEntity(updateDto);
        entityToUpdate.setId(id);

        ProjectEntity savedEntity = projectRepository.save(entityToUpdate);

        return projectMapper.mapEntityToGetDto(savedEntity);
    }

    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project with id " + id + " not found");
        }

        projectRepository.deleteById(id);
    }
}