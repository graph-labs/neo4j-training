package io.github.fbiville.trainings.neo4j._1_core_api;

import io.github.fbiville.trainings.neo4j.internal.GraphTests;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.graphdb.event.TransactionEventHandler;

import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class focuses on the creation and use of transaction event handlers, a.k.a. triggers.
 */
public class _4_TriggerTest extends GraphTests {

    private CountTransactionEventHandler nameCountTrigger;

    @Before
    public void prepare() {
        nameCountTrigger = new CountTransactionEventHandler(); /*TODO: add missing code in this class (below)*/
        graphDb.registerTransactionEventHandler(nameCountTrigger);
    }

    @Test
    public void should_count_name_properties() throws Exception {
        try (Transaction transaction = graphDb.beginTx()) {
            Node character = graphDb.createNode(Label.label("Character"));
            character.setProperty("name", "Guybrush");
            Node videoGame = graphDb.createNode(Label.label("Video Game"));
            videoGame.setProperty("title", "Monkey Island");

            Relationship relationship = character.createRelationshipTo(videoGame, RelationshipType.withName("APPEARED_IN"));
            relationship.setProperty("name", "Guybrush Threepwood");
            transaction.success();
        }

        try (Transaction ignored = graphDb.beginTx()) {
            Node node = graphDb.createNode();
            node.setProperty("name", "fail");
        }

        try (Transaction ignored = graphDb.beginTx()) {
            assertThat(nameCountTrigger.namePropertyToCommitCount()).isEqualTo(2);
            assertThat(nameCountTrigger.getCommittedNamePropertyCount()).isEqualTo(2);
        }
    }

    /**
     * Counts the number of name properties in node and relationships.
     * This holds a separate count for committed and rollbacked properties.
     *
     * As noted in {@link TransactionEventHandler} documentation, an handler can only listen
     * to write transactions that commits. Rollback (explicitly or by absence of commit) will
     * not be sent to the handler.
     */
    private static class CountTransactionEventHandler implements TransactionEventHandler<Long> {

        private final AtomicLong namePropertiesToCommit = new AtomicLong(0);
        private final AtomicLong committedNameProperties = new AtomicLong(0);

        @Override
        public Long beforeCommit(TransactionData data) throws Exception {
            //TODO: count the number of properties 'name' to commit
            return -1L;
        }

        @Override
        public void afterCommit(TransactionData data, Long seenBeforeCommit) {
            //TODO: count the number of properties 'name' that are committed
        }

        @Override
        public void afterRollback(TransactionData data, Long seenBeforeCommit) {
        }

        public long namePropertyToCommitCount() {
            return namePropertiesToCommit.get();
        }

        public long getCommittedNamePropertyCount() {
            return committedNameProperties.get();
        }
    }
}
