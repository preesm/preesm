package org.preesm.algorithm.refining;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.Parameter;

// Comparer les Hash pour vérifier qu'on préserve la validité des graphes

public class ExecutionCheck {
  String        filePath;
  String        projectFullPath;
  String        functionContent;
  AbstractActor actor;
  StringBuilder splitfunc;
  StringBuilder splitHeader;
  String        bufferIterator;
  Long          actorTim = 0L;
  Long          initTim  = 0L;
  Long          loopTim  = 0L;
  Long          endTim   = 0L;
  List<Long>    timings;
  String        forLoop;

  public ExecutionCheck(String filePath, String projectFullPath, String functionContent, AbstractActor actor,
      StringBuilder splitfunc, StringBuilder splitHeader, List<Long> timings, String bufferIterator, String forLoop) {
    this.filePath = filePath;
    this.projectFullPath = projectFullPath;
    this.functionContent = functionContent;
    this.actor = actor;
    this.splitfunc = splitfunc;
    this.splitHeader = splitHeader;
    this.timings = timings;
    this.bufferIterator = bufferIterator;
    this.forLoop = forLoop;
  }

  public boolean execute() {
    final String fileName = "main_testMD5_" + actor.getName() + ".c";
    printMain(fileName);

    return compileAndRun(projectFullPath + filePath + fileName);

  }

  private void printMain(String fileName) {
    // PreesmIOHelper.getInstance().deleteFile(filePath + fileName);
    // Chemin du fichier C à générer

    final FunctionPrototype loopPrototype = ((CHeaderRefinement) ((Actor) actor).getRefinement()).getLoopPrototype();
    final String funcArgs = loopPrototype.getInputArguments().stream().map(arg -> arg.getType() + " *" + arg.getName())
        .collect(Collectors.joining(", "))
        + loopPrototype.getOutputArguments().stream().map(arg -> arg.getType() + " *" + arg.getName())
            .collect(Collectors.joining(", "));
    final String functionContentClean = clean();
    final String splitfuncClean = clean2();
    String actorArgs = "    ";
    for (final ConfigInputPort cfg : actor.getConfigInputPorts()) {
      actorArgs += "int " + cfg.getName() + " ="
          + ((Parameter) cfg.getIncomingDependency().getSetter()).getExpression().evaluate() + ";\n";

    }
    for (final DataInputPort p : actor.getDataInputPorts()) {
      actorArgs += p.getFifo().getType() + " *" + p.getName() + "= malloc(" + p.getExpression().getExpressionAsString()
          + "*sizeof(" + p.getFifo().getType() + "));\n";
    }
    for (final DataOutputPort p : actor.getDataOutputPorts()) {
      actorArgs += p.getFifo().getType() + " *" + p.getName() + "= malloc(" + p.getExpression().getExpressionAsString()
          + "*sizeof(" + p.getFifo().getType() + "));\n";
    }

    final String funcArgs2 = loopPrototype.getInputConfigParameters().stream().map(arg -> " " + arg.getName())
        .collect(Collectors.joining(", ")) + ","
        + loopPrototype.getInputArguments().stream().map(arg -> " " + arg.getName()).collect(Collectors.joining(", "))
        + ","
        + loopPrototype.getOutputArguments().stream().map(arg -> " " + arg.getName()).collect(Collectors.joining(", "));
    final String funcArgs3 = loopPrototype.getInputArguments().stream().map(arg -> " " + arg.getName() + "_out")
        .collect(Collectors.joining(", ")) + ","
        + loopPrototype.getOutputArguments().stream().map(arg -> " " + arg.getName() + "_out")
            .collect(Collectors.joining(", "));
    String actorFiring = "    ";
    actorFiring += loopPrototype.getName() + "(" + funcArgs2 + ");\n";
    final String arg = actor.getDataOutputPorts().get(0).getName();
    String splitArgs = "    ";
    for (final DataInputPort p : actor.getDataInputPorts()) {
      splitArgs += p.getFifo().getType() + " *" + p.getName() + "_out" + "= malloc("
          + p.getExpression().getExpressionAsString() + "*sizeof(" + p.getFifo().getType() + "));\n";
    }
    for (final DataOutputPort p : actor.getDataOutputPorts()) {
      splitArgs += p.getFifo().getType() + " *" + p.getName() + "_out" + "= malloc("
          + p.getExpression().getExpressionAsString() + "*sizeof(" + p.getFifo().getType() + "));\n";
    }
    String initFiring = "    ";
    initFiring += loopPrototype.getName() + "_init(" + funcArgs2 + ");\n";
    String loopFiring = "    ";
    loopFiring += loopPrototype.getName() + "_loop(" + funcArgs2 + "," + funcArgs3 + "," + bufferIterator + ");\n";
    String endFiring = "    ";
    endFiring += loopPrototype.getName() + "_end(" + loopPrototype.getInputConfigParameters().stream()
        .map(a -> " " + a.getName()).collect(Collectors.joining(", ")) + "," + funcArgs3 + ");\n";
    final String arg2 = actor.getDataOutputPorts().get(0).getName();

    // Contenu du fichier C
    final String cCode = """
        #include <stdio.h>
        #include <string.h>
        #include <openssl/evp.h>
        #include <stdbool.h>

        typedef unsigned char uchar;


        // Déclaration de la fonction


        """ + functionContentClean.replace("OUT", "") + "\n" + splitfuncClean + "\n" + """

        // Fonction utilitaire pour calculer le MD5

        int compute_md5(const void *data, size_t data_len, unsigned char *md5, unsigned int *md5_len) {
            EVP_MD_CTX *mdctx = EVP_MD_CTX_new();
            if (mdctx == NULL) {
                fprintf(stderr, "Failed to create MD5 context\\n");
                return 1;
            }

            if (EVP_DigestInit_ex(mdctx, EVP_md5(), NULL) != 1 ||
                EVP_DigestUpdate(mdctx, data, data_len) != 1 ||
                EVP_DigestFinal_ex(mdctx, md5, md5_len) != 1) {
                fprintf(stderr, "Failed to compute MD5 hash\\n");
                EVP_MD_CTX_free(mdctx);
                return 1;
            }

            EVP_MD_CTX_free(mdctx);
            return 0;
        }

        // Fonction utilitaire pour afficher un hash MD5
        void print_md5(const unsigned char *md5, unsigned int md5_len) {
            for (unsigned int i = 0; i < md5_len; i++) {
                printf("%02x", md5[i]);
            }
            printf("\\n");
        }

        // Comparaison des deux hashes MD5
        bool compare_md5(const unsigned char *md5_1, const unsigned char *md5_2, unsigned int md5_len) {
            int cmp_result = memcmp(md5_1, md5_2, md5_len);
            printf("memcmp result: %d\\n", cmp_result);

            return memcmp(md5_1, md5_1, md5_len) == 0;
        }

        // Fonction principale
        int main() {
                """ + actorArgs + """

            unsigned char md5_1[EVP_MAX_MD_SIZE], md5_2[EVP_MAX_MD_SIZE];
            unsigned int md5_len_1, md5_len_2;
            struct timespec startGlobal, endGlobal;
            struct timespec startInit, endInit;
            struct timespec startLoop, endLoop;
            struct timespec startEnd, endEnd;

            // Appel de l'acteur splitté
            clock_gettime(CLOCK_MONOTONIC, &startGlobal);
        """ + actorFiring + """
        clock_gettime(CLOCK_MONOTONIC, &endGlobal);
        long globalTime = (endGlobal.tv_sec - startGlobal.tv_sec) * 1e9 + (endGlobal.tv_nsec - startGlobal.tv_nsec);
        printf("Global Time: %ld ns\\n", globalTime);

        if (compute_md5(&""" + arg + """
        , sizeof(""" + arg + """
        ), md5_1, &md5_len_1) == 0) {
            printf(\"MD5 hash of the result: \");
            print_md5(md5_1, md5_len_1);
        }

        // Appel des fonction split\n""" + splitArgs + """

        clock_gettime(CLOCK_MONOTONIC, &startInit);\n""" + initFiring + """
        clock_gettime(CLOCK_MONOTONIC, &endInit);

        """ + forLoop + """

        clock_gettime(CLOCK_MONOTONIC, &startLoop);\n""" + loopFiring + """
            clock_gettime(CLOCK_MONOTONIC, &endLoop);
        }
        clock_gettime(CLOCK_MONOTONIC, &startEnd);\n""" + endFiring + """
        clock_gettime(CLOCK_MONOTONIC, &endEnd);

        long initTime = (endInit.tv_sec - startInit.tv_sec) * 1e9 + (endInit.tv_nsec - startInit.tv_nsec);
        long loopTime = (endLoop.tv_sec - startLoop.tv_sec) * 1e9 + (endLoop.tv_nsec - startLoop.tv_nsec);
        long endTime = (endEnd.tv_sec - startEnd.tv_sec) * 1e9 + (endEnd.tv_nsec - startEnd.tv_nsec);

        printf("Global Time: %ld ns\\n", globalTime);
        printf("Init Time: %ld ns\\n", initTime);
        printf("Loop Time: %ld ns\\n", loopTime);
        printf("End Time: %ld ns\\n", endTime);

        if (compute_md5(&""" + arg2 + """
        , sizeof(""" + arg2 + """
            ), md5_2, &md5_len_2) == 0) {
                printf("MD5 hash of the result2: ");
                print_md5(md5_2, md5_len_2);
            }

                      // Vérification des deux MD5
            if (!compare_md5(md5_1, md5_2, md5_len_1)) {
                fprintf(stderr, "Error: MD5 hashes do not match!\\n");
                return 1; // Retourne 1 si les hashes sont différents
            }

            return 0; // Retourne 0 si les hashes sont identiques
        }

                            """;
    PreesmIOHelper.getInstance().print(filePath, fileName, cCode);

  }

  private String clean() {

    // Regex pour trouver les appels de fonction
    final String functionCallRegex = "\\b([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\([^;]*\\);";
    final Pattern pattern = Pattern.compile(functionCallRegex);

    // Nouvelle chaîne pour le contenu avec commentaires
    final StringBuilder commentedContent = new StringBuilder();
    final String[] lines = functionContent.split("\n");

    // Pour chaque ligne de code, on vérifie si c'est un appel de fonction
    for (final String line : lines) {
      final Matcher matcher = pattern.matcher(line);

      // Si un appel de fonction est trouvé, commenter la ligne entière
      if (matcher.find()) {
        final String commentedLine = "// " + line; // Commente toute la ligne
        commentedContent.append(commentedLine).append("\n");
      } else {
        // Sinon, ajouter la ligne sans modification
        commentedContent.append(line).append("\n");
      }
    }
    // return functionContent;
    return commentedContent.toString();
  }

  private String clean2() {
    final String string = splitfunc.toString();

    // Regex pour trouver les appels de fonction dans un bloc, sauf memcpy
    final String functionCallRegex = "\\b((?!(memcpy|sizeof))[a-zA-Z_][a-zA-Z0-9_]*)\\s*\\([^;]*\\);";

    // Nouvelle chaîne pour stocker le contenu modifié
    final StringBuilder commentedContent = new StringBuilder();

    final String[] lines = string.split("\n");

    for (final String line : lines) {
      // Parcourir chaque bloc
      final Pattern functionCallPattern = Pattern.compile(functionCallRegex);
      final Matcher functionCallMatcher = functionCallPattern.matcher(line);

      // Modifier les appels de fonction en les commentant
      if (functionCallMatcher.find()) {
        final String commentedLine = "// " + line; // Ajoute le commentaire
        commentedContent.append(commentedLine).append("\n");
      } else {
        // Sinon, ajouter la ligne sans modification
        commentedContent.append(line).append("\n");
      }
    }
    // return splitfunc.toString();
    return commentedContent.toString();
  }

  private boolean compileAndRun(String cFilePath) {

    try {
      // Commande pour compiler le fichier C
      final String compileCommand = "gcc -o main " + cFilePath + " -lssl -lcrypto";

      // Commande pour exécuter le binaire compilé
      final String runCommand = "./main";

      // Compilation
      System.out.println("Compilation en cours...");
      final Process compileProcess = new ProcessBuilder("bash", "-c", compileCommand).inheritIO().start();
      compileProcess.waitFor();

      // Exécution
      System.out.println("Exécution du programme...");
      final Process runProcess = new ProcessBuilder("bash", "-c", runCommand).start();

      // StringBuilder pour capturer stdout et stderr
      final StringBuilder stdoutBuilder = new StringBuilder();
      final StringBuilder stderrBuilder = new StringBuilder();

      // Lire la sortie standard (stdout)
      final Thread stdoutThread = new Thread(() -> {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()))) {
          String line;
          while ((line = reader.readLine()) != null) {
            stdoutBuilder.append(line).append("\n");

            if (line.startsWith("Global Time: ")) {
              final String actorTimStr = line.replace("Global Time: ", "").replace(" ns", "");
              actorTim = Long.valueOf(actorTimStr);
            }
            if (line.startsWith("Init Time: ")) {
              final String initTimStr = line.replace("Init Time: ", "").replace(" ns", "");
              initTim = Long.valueOf(initTimStr);

            }
            if (line.startsWith("Loop Time: ")) {
              final String loopTimStr = line.replace("Loop Time: ", "").replace(" ns", "");
              loopTim = Long.valueOf(loopTimStr);

            }
            if (line.startsWith("End Time: ")) {
              final String endTimStr = line.replace("End Time: ", "").replace(" ns", "");
              endTim = Long.valueOf(endTimStr);

            }
          }

        } catch (final Exception e) {
          e.printStackTrace();
        }
      });
      // Lire la sortie d'erreur (stderr)
      final Thread stderrThread = new Thread(() -> {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()))) {
          String line;
          while ((line = reader.readLine()) != null) {
            stderrBuilder.append(line).append("\n");
          }
        } catch (final Exception e) {
          e.printStackTrace();
        }
      });

      stdoutThread.start();
      stderrThread.start();
      // Attendre que le processus se termine
      final int exitCode = runProcess.waitFor();
      stdoutThread.join();

      // Stocker les sorties dans des Strings
      final String stdout = stdoutBuilder.toString();
      final String stderr = stderrBuilder.toString();

      // // Afficher les résultats
      // System.out.println("STDOUT:");
      // System.out.println(stdout);
      if (!stderr.isEmpty() || (initTim + loopTim + endTim > actorTim)) {
        return false;
      }

    } catch (IOException | InterruptedException e) {
      System.err.println("Erreur lors de la compilation ou de l'exécution : " + e.getMessage());
      return false;
    }

    timings.add(actorTim);
    timings.add(initTim);
    timings.add(loopTim);
    timings.add(endTim);

    return true;
  }

}
