import groovy.json.JsonSlurper
import org.sonatype.nexus.capability.CapabilityReference
import org.sonatype.nexus.capability.CapabilityType
import org.sonatype.nexus.internal.capability.DefaultCapabilityReference
import org.sonatype.nexus.internal.capability.DefaultCapabilityRegistry

parsed_args = new JsonSlurper().parseText(args)

if (parsed_args.capability_typeId == "rapture.branding") {
    parsed_args.capability_properties['headerEnabled'] = parsed_args.capability_properties['headerEnabled'].toString()
    parsed_args.capability_properties['footerEnabled'] = parsed_args.capability_properties['footerEnabled'].toString()
}
// init if empty
if (!parsed_args.capability_properties) {
  parsed_args.capability_properties = [:]
}


def capabilityRegistry = container.lookup(DefaultCapabilityRegistry.class.getName())
def capabilityType = CapabilityType.capabilityType(parsed_args.capability_typeId)

DefaultCapabilityReference existing = capabilityRegistry.all.find { CapabilityReference capabilityReference ->
    capabilityReference.context().descriptor().type() == capabilityType
}

if (existing) {
    def active = existing.isActive() ?: true
    log.info(parsed_args.capability_typeId + ' capability updated to: {}',
        capabilityRegistry.update(existing.id(), active, existing.notes(), parsed_args.capability_properties).toString()
    )
}
else {
    log.info(parsed_args.capability_typeId + ' capability created as: {}',
        capabilityRegistry.add(capabilityType, true, 'configured through api', parsed_args.capability_properties).toString()
    )
}
