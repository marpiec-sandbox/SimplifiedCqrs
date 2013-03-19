package pl.marpiec.simplifiedcqrs

import spock.lang.Specification

class UIDSpec extends Specification{

    def "uid generates proper hash and equal works fine"() {

        given:
        def uid = new UID(id)
        def sameUid = new UID(id)
        def otherUid = new UID(otherId)

        expect:
        uid.hashCode() == sameUid.hashCode()
        uid.equals(sameUid)

        !uid.equals(otherUid)

        where:
        id    | otherId
        23    | 42
        2     | 4
        345   | 855
        -6    | 5
        3     | 0
        100   | 101

    }


    def "uid can be converted to String"() {
        given:
        def uid = new UID(id)

        expect:
        uid.toString().equals(uidString)

        where:
        id    | uidString
        3     | "3"
        -4    | "-4"
        134   | "134"
        0     | "0"
    }

    def "uids can be compared"() {
        given:
        def uid1 = new UID(id1)
        def uid2 = new UID(id2)

        expect:
        uid1.compareTo(uid2) == result

        where:
        id1   | id2   | result
        0     | 4     | -1
        -4    | 14    | -1
        100   | 17    | 1
        4     | 3     | 1
        4     | 4     | 0
        -6    | -6    | 0
    }

    def "string can be parsed to uid"() {
        expect:
        UID.parseOrZero(uidString).equals(new UID(id))

        where:
        uidString | id
        "5"       | 5
        "634"     | 634
        "-54"     | -54
        "0"       | 0
        "sdrg"    | 0
        "4.5"     | 0
    }
}
