// Rel native launcher for MacOS.

#include <iostream>
#include <cstdlib>
#include <unistd.h>
#include <libgen.h>
#include <string>
#include <fstream>
#include <streambuf>

int main(int argc, char **argv)
{
  // Convert first argument of argv[0] (full pathspec to this executable) to path where executable is found
  char *dir = dirname(argv[0]);
  chdir(dir);

  // Read the ini file
  std::string iniFileName("lib/Rel.ini");
  std::ifstream configfile(iniFileName);
  std::string cmd((std::istreambuf_iterator<char>(configfile)), std::istreambuf_iterator<char>());

  // Empty or no ini file?
  if (cmd.length() == 0) {
    std::cerr << (std::string("Missing or Damaged .ini File: Unable to find ") + iniFileName).c_str() << std::endl;
    return 10;
  }

  return system(cmd.c_str());
}