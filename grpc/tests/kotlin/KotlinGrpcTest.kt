import MyGame.Example.Monster
import MyGame.Example.MonsterStorageGrpc
import MyGame.Example.MonsterStorageGrpcKt
import MyGame.Example.Stat
import com.google.common.truth.Truth.assertThat
import com.google.flatbuffers.FlatBufferBuilder
import io.grpc.ManagedChannel
import io.grpc.Server
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import java.io.IOException
import java.util.concurrent.TimeUnit.SECONDS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class KotlinGrpcTest {

  @Before fun setUp() {}

  @Test
  fun storeAndRetrieve_success() {
    val monster = GameFactory.createMonster("monster", 100)
    val monsters = mutableMapOf<String, MutableList<Monster>>()
    val service = MonsterService(monsters)

    val serverName = InProcessServerBuilder.generateName()
    val server = service.start(serverName)
    val channel = InProcessChannelBuilder.forName(serverName).directExecutor().build()
    val stub = MonsterStorageGrpcKt.MonsterStorageCoroutineStub(channel)

    val response = runBlocking { stub.store(monster) }

    assertThat(response.id).isEqualTo("monster_hp")
    assertThat(response.val_).isEqualTo(100L)
    assertThat(channel.shutdown().awaitTermination(5, SECONDS)).isTrue()
    assertThat(server.shutdown().awaitTermination(5, SECONDS)).isTrue()
  }

  internal class MonsterService(val monsters: MutableMap<String, MutableList<Monster>>) :
    MonsterStorageGrpcKt.MonsterStorageCoroutineImplBase() {
    private val defaultStat = GameFactory.createStat("default", 0L, 0u)

    override suspend fun store(request: Monster): Stat {
      println("Store: $request")
      val id = request.name + "_hp"
      val stat = GameFactory.createStat(id, request.hp.toLong(), 1u)
      if (monsters[id] == null) {
        monsters[id] = mutableListOf()
      }
      monsters[id]?.add(request)
      return stat
    }

    override fun retrieve(request: Stat): Flow<Monster> {
      val monsterList = monsters[request.id]
      if (monsterList == null) {
        throw IllegalArgumentException("Monster not found")
      }
      return monsterList.asFlow()
    }

    @Throws(IOException::class, InterruptedException::class)
    fun start(serverName: String): Server {
      return InProcessServerBuilder.forName(serverName)
        .directExecutor()
        .addService(this)
        .build()
        .start()
    }
  }

  companion object GameFactory {
    fun createMonster(name: String, hp: Short): Monster {
      val builder = FlatBufferBuilder(0)
      val nameOffset = builder.createString(name)
      Monster.startMonster(builder)
      Monster.addName(builder, nameOffset)
      Monster.addHp(builder, hp)
      val monsterOffset = Monster.endMonster(builder)
      builder.finish(monsterOffset)
      val monster = Monster.getRootAsMonster(builder.dataBuffer())
      return monster
    }

    fun createStat(id: String, value: Long, count: UShort): Stat {
      val builder = FlatBufferBuilder(0)
      val idOffset = builder.createString(id)
      val statOffset = Stat.createStat(builder, idOffset, value, count)
      builder.finish(statOffset)
      val stat = Stat.getRootAsStat(builder.dataBuffer())
      return stat
    }
  }
}
