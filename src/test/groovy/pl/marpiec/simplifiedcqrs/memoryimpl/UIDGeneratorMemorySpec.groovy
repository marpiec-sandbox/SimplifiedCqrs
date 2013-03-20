package pl.marpiec.simplifiedcqrs.memoryimpl

import pl.marpiec.simplifiedcqrs.UID
import spock.lang.Specification

class UIDGeneratorMemorySpec extends Specification {

    def "generator should create different uids each call"() {

        given:
        def testSize = 1000;
        def uidGenerator = new UIDGeneratorMemoryImpl()
        def uidSet = new HashSet<UID>()
        for (i in 1..testSize) {
            uidSet.add(uidGenerator.nextUID())
        }

        expect:
        uidSet.size() == testSize

    }

}
