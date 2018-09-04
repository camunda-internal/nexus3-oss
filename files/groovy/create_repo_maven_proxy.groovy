import groovy.json.JsonSlurper
import org.sonatype.nexus.repository.Repository
import org.sonatype.nexus.repository.config.Configuration

parsed_args = new JsonSlurper().parseText(args)

repositoryManager = repository.repositoryManager

authentication = parsed_args.remote_username == null ? null : [
        type: 'username',
        username: parsed_args.remote_username,
        password: parsed_args.remote_password
]

Repository existingRepository = repositoryManager.get(parsed_args.name)

if (existingRepository != null) {

    newConfig = existingRepository.configuration.copy()
    // We only update values we are allowed to change (cf. greyed out options in gui)
    newConfig.attributes['maven']['versionPolicy'] = parsed_args.version_policy.toUpperCase()
    newConfig.attributes['maven']['layoutPolicy'] = parsed_args.layout_policy.toUpperCase()
    newConfig.attributes['proxy']['remoteUrl'] = parsed_args.remote_url
    newConfig.attributes['proxy']['contentMaxAge'] = parsed_args.content_max_age
    newConfig.attributes['proxy']['metadataMaxAge'] = parsed_args.metadata_max_age
    newConfig.attributes['httpclient']['authentication'] = authentication
    newConfig.attributes['storage']['strictContentTypeValidation'] = Boolean.valueOf(parsed_args.strict_content_validation)
    newConfig.attributes['negativeCache']['timeToLive'] = parsed_args.time_to_live

    repositoryManager.update(newConfig)

} else {

    configuration = new Configuration(
            repositoryName: parsed_args.name,
            recipeName: 'maven2-proxy',
            online: true,
            attributes: [
                    maven  : [
                            versionPolicy: parsed_args.version_policy.toUpperCase(),
                            layoutPolicy : parsed_args.layout_policy.toUpperCase()
                    ],
                    proxy  : [
                            remoteUrl: parsed_args.remote_url,
                            contentMaxAge: parsed_args.content_max_age,
                            metadataMaxAge: parsed_args.metadata_max_age
                    ],
                    httpclient: [
                            blocked: false,
                            autoBlock: true,
                            authentication: authentication,
                            connection: [
                                    useTrustStore: false
                            ]
                    ],
                    storage: [
                            blobStoreName: parsed_args.blob_store,
                            strictContentTypeValidation: Boolean.valueOf(parsed_args.strict_content_validation)
                    ],
                    negativeCache: [
                            enabled: true,
                            timeToLive: parsed_args.time_to_live
                    ]
            ]
    )

    repositoryManager.create(configuration)

}
