# Implementation
readme md reference </br>
https://medium.com/analytics-vidhya/how-to-create-a-readme-md-file-8fb2e8ce24e3 </br>
maven publish reference </br>
https://www.youtube.com/watch?v=tr5_OWgXDiw&ab_channel=Niiblesnovice
https://www.albertgao.xyz/2018/01/18/how-to-publish-artifact-to-maven-central-via-gradle

Required Base class

- BaseRestController
- BaseService
- BaseException
- ResponseConverter


  Exception

- AccessDeniedFeatureException
- ExternalSourceException
- ParameterException

***
#### Create Request

---
request extend BaseRequest

```java 

//  extend BaseRequest
@Data
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CompanyHeadCountRequest extends BaseRequest {

    @MandatoryField
    @NPWP
    @NotNullAndEmpty
    private String npwpCompany;

    @NotNullAndEmpty
    private String companyName;

}

```

#### Create Controller
request extend BaseRestController

```java
//  extend BaseRestController
@Slf4j
@RestController
@RequestMapping(value = "/v2/{slug}")
@SuppressWarnings({"unchecked", "rawtypes"})
public class CompanyCountController extends BaseRestController {
    
}
```

---
#### Create Service

request extend BaseService
```java
// extend BaseService
@Slf4j
@Service
@SuppressWarnings("rawtypes")
public class CompanyHeadCountServiceImpl extends BaseService implements CompanyHeadCountService {

    // extend ResponseConverter
    @Autowired
    protected CompanyEmployeeCountConverter companyHeadConverter;

    @Override
    public ServiceResolver verifyEmployeeCount(CustomerManifest manifest, CompanyHeadCountRequest request) {
        // initial object
        CompanyEmployeeCountWrapper wrapper = new CompanyEmployeeCountWrapper(companyHeadConverter, request);
        // validate request, will ParameterException rollback transaction
        validate(manifest, wrapper, request);
        try {
            SourceBaseResponse<NpwpResponse> response = getForObject(sourceEmployeeCount + "?npwp=" + request.getNpwpCompany(), new ParameterizedTypeReference<SourceBaseResponse<NpwpResponse>>() {
            });
            wrapper.setProbe(response.getData());
            // return deduct
            return successTrue(manifest, wrapper);
        } catch (HttpStatusCodeException e) {
            handlerHttpErrorCode(e, manifest, wrapper, request);
            log.error("error http code", e);
            if (wrapper.errorNotFound()) {
                // return deduct
                return successTrue(manifest, wrapper);
            }
            // throw exception and rollback
            throw new ExternalSourceException(manifest, wrapper);
        } catch (ResourceAccessException e) {
            log.error("error access external", e);
            // throw exception and rollback
            throw new ExternalSourceException(manifest, wrapper);
        }
    }
    
}
```

---
#### Create Wrapper
```java
// extends BaseServiceWrapper
@Data
@EqualsAndHashCode(callSuper = true)
public class CompanyEmployeeCountWrapper extends BaseServiceWrapper<CompanyHeadCountRequest> {

    private NpwpResponse probe;

    public CompanyEmployeeCountWrapper(ResponseConverter converter, CompanyHeadCountRequest request ) {
        super(converter, request);
    }

}

```

---
#### Create Converter

define converter for response and set default value from code

```java
// extend ResponseConverter
@Component
public class CompanyEmployeeCountConverter extends ResponseConverter<CompanyHeadCountResponse, CompanyEmployeeCountWrapper> {

    @Override
    public CompanyHeadCountResponse convert(CompanyEmployeeCountWrapper param) {
        CompanyHeadCountRequest source = param.getParamBody();
        NpwpResponse probe = param.getProbe();
        Map<String, String> errors = param.getErrors();
        CompanyHeadCountResponse response = new CompanyHeadCountResponse();
        // name
        if (ValueOf.nonNull(source.getCompanyName())) {
            response.setCompanyName(equals(source.getCompanyName(), probe.getName()));
            if (!response.getCompanyName()) {
                response.setCompanyPercentage(Str.similarityText(source.getCompanyName(), probe.getName()));
            }
        }
        // count
        if (Objects.nonNull(probe.getCount())) {
            response.setGrade(grade(probe.getCount()));
        }
        // set default null if error key by field
        if (errors.containsKey("company_name")) response.setCompanyName(null);
        return response;
    }


    private ResponseConstants.Grade grade(int count) {
        if (ValueOf.minMaxLength(0, 10, count)) {
            return ResponseConstants.Grade.A;
        }
        ...
        ...
        return ResponseConstants.Grade.J;
    }
}
```

