// Rel native launcher for Windows.

#include "pch.h"
#include <cstdlib>
#include <direct.h>

#include <Windows.h>

int APIENTRY wWinMain(_In_ HINSTANCE hInstance,
	_In_opt_ HINSTANCE hPrevInstance,
	_In_ LPWSTR    lpCmdLine,
	_In_ int       nCmdShow)
{
	system("jre\\bin\\java -splash:lib\\Splash.png -cp \"lib\\*;lib\\nattable\\*;lib\\swt\\*;lib\\swt\\win_64\\*\" DBrowser ");
}
