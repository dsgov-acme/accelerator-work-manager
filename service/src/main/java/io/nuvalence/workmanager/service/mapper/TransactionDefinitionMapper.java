package io.nuvalence.workmanager.service.mapper;

import io.nuvalence.workmanager.service.domain.transaction.TransactionDefinition;
import io.nuvalence.workmanager.service.generated.models.TransactionDefinitionCreateModel;
import io.nuvalence.workmanager.service.generated.models.TransactionDefinitionExportModel;
import io.nuvalence.workmanager.service.generated.models.TransactionDefinitionResponseModel;
import io.nuvalence.workmanager.service.generated.models.TransactionDefinitionUpdateModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Maps transaction definitions between the following 2 forms.
 *
 * <ul>
 *     <li>API Models (
 *      {@link io.nuvalence.workmanager.service.generated.models.TransactionDefinitionUpdateModel}
 *         and {@link io.nuvalence.workmanager.service.generated.models.TransactionDefinitionResponseModel})</li>
 *     <li>Logic/Persistence Model
 *     ({@link io.nuvalence.workmanager.service.domain.transaction.TransactionDefinition})</li>
 * </ul>
 */
@Mapper(componentModel = "spring")
public interface TransactionDefinitionMapper extends LazyLoadingAwareMapper {
    TransactionDefinitionMapper INSTANCE = Mappers.getMapper(TransactionDefinitionMapper.class);

    TransactionDefinitionResponseModel transactionDefinitionToResponseModel(
            TransactionDefinition value);

    TransactionDefinition updateModelToTransactionDefinition(
            TransactionDefinitionUpdateModel model);

    TransactionDefinition createModelToTransactionDefinition(
            TransactionDefinitionCreateModel model);

    /**
     * Converts a TransactionDefinition to a TransactionDefinitionExportModel.
     *
     * @param value           the TransactionDefinition.
     * @return the TransactionDefinitionExportModel.
     */
    default TransactionDefinitionExportModel
            transactionDefinitionToTransactionDefinitionExportModel(TransactionDefinition value) {
        if (value == null) {
            return null;
        }

        TransactionDefinitionExportModel model = new TransactionDefinitionExportModel();
        model.setId(String.valueOf(value.getId()));
        model.setKey(value.getKey());
        model.setName(value.getName());
        model.setProcessDefinitionKey(value.getProcessDefinitionKey());
        model.setSchema(value.getSchemaKey());
        model.setDefaultStatus(value.getDefaultStatus());
        model.setCategory(value.getCategory());
        model.setDefaultFormConfigurationKey(value.getDefaultFormConfigurationKey());

        return model;
    }
}
