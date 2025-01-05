package apiModels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pet {
    private Long id;
    private Category category;
    private String name;
    private List<String> photoUrls;
    private List<Tag> tags;
    private String status;

    public Pet() {
    }

    public Pet(Long id, Category category, String name, List<String> photoUrls, List<Tag> tags, String status) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.photoUrls = photoUrls;
        this.tags = tags;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return Objects.equals(id, pet.id) &&
                Objects.equals(name, pet.name) &&
                Objects.equals(status, pet.status) &&
                Objects.equals(category, pet.category) &&
                Objects.equals(photoUrls, pet.photoUrls) &&
                Objects.equals(tags, pet.tags);
    }

    public Pet copy() {
        List<String> copiedPhotoUrls = (photoUrls != null) ? new ArrayList<>(photoUrls) : null;
        List<Tag> copiedTags = (tags != null) ? new ArrayList<>(tags) : null;

        return new Pet(
                this.id,
                this.category != null ? this.category.copy() : null, // Если Category имеет метод copy
                this.name,
                copiedPhotoUrls,
                copiedTags,
                this.status
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status, category, photoUrls, tags);
    }
}
