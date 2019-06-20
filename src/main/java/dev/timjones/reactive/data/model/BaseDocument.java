package dev.timjones.reactive.data.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

@Data
@Getter
@Setter
public class BaseDocument implements Serializable {

    @Id
    private ObjectId id;

    @CreatedBy
    @Field("created_by")
    private String createdBy;

    @CreatedDate
    @Field("created_date")
    private Date createdDate;

    @LastModifiedBy
    @Field("updated_by")
    private String updatedBy;

    @LastModifiedDate
    @Field("updated_date")
    private Date updatedDate;

    @Version
    @Field("version")
    private Long version;

    private Boolean delete = Boolean.FALSE;
}
