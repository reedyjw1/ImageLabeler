import com.reedy.imagelabeler.features.annotations.repository.AnnotationsRepository
import com.reedy.imagelabeler.features.annotations.repository.IAnnotationsRepository
import org.koin.dsl.module

val appDependencies = module {
    // Singleton (returns always the same unique instance of the object)
    single<IAnnotationsRepository> { AnnotationsRepository(get()) }
}