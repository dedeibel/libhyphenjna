#!/usr/bin/perl
# Original author: Flemming Frandsen (flfr at stibo dot com / ff at nrvissing dot net)
# From package: https://github.com/dren-dk/HunspellJNA

use strict;
use warnings;
use POSIX;
use FindBin qw'$Bin';
use File::Copy qw'cp';

my ($sysname, $nodename, $release, $version, $machine) = POSIX::uname();
print "Running under $sysname on $machine\n";

chdir $Bin;
system("ant") and die "Must be able to build.";
print "Done building java code\n";

my $libName = `java -cp build/jar/hyphen.jar name.benjaminpeter.hyphen.HyphenMain -libname`;
die "Don't know this platform, implement support for this platform in Hyphen.java (and possibly Hyphen and JNA)"
	unless $libName;
chomp $libName;
print "Going to try to build $libName\n";
$libName = "$Bin/native-lib/$libName";

die "Whoa there, cowboy, $libName already exists, delete it if you want to rebuild!" if -f $libName;

# Untar and apply the diffs found.

sub untar() {
	opendir D, "$Bin/native-src" or die "Urgh: $!";
	my @nsf = readdir D;
	closedir D;

	my ($hyphtar) = grep {/^hyphen-.*\.tar\.gz$/} @nsf; 
	die "Unable to find the hyphen tar file in native-src" unless $hyphtar;

	chdir "$Bin/native-src";
	system("tar xfz $hyphtar") and die "Unable to untar $hyphtar";

	# Now find the source dir:
	opendir D, "$Bin/native-src" or die "Urgh: $!";
	my ($hyphdir) = grep {-d $_ and /^hyphen-/} readdir D;
	closedir D;

	die "Unable to find the hyphen source dir" unless $hyphdir;

	for my $p (grep {/\.diff$/} @nsf) {
		system("patch -u -p 2 -d $hyphdir -i ../$p") and die "Unable to apply patch: $p";
	}
	
	chdir "$Bin/native-src/$hyphdir";
	return "$Bin/native-src/$hyphdir";
}

mkdir "$Bin/native-lib";

my $unixy = 0;
if ($sysname eq 'Linux') {
  if ($machine eq 'i686' or $machine eq 'x86_64') {
    $unixy = 1;
  } else {
    die "The architecture $machine is not supported, please fix native-build.pl";		
  }
    
} elsif ($sysname eq 'Darwin') {
  if ($machine eq 'Power Macintosh') {
    $unixy = 1;
  } elsif ($machine eq 'i386') {
    $unixy = 1;
  } else {
    die "The architecture $machine is not supported, please fix native-build.pl";		
  }
} 

if ($unixy) { # aah, a sane OS, so life is simple. True.
    my $ns = untar;	
    system("./configure && make") and die "Unable to configure and make, please fix";  
    
    my $outputDir = "$ns/.libs";
    chdir $outputDir or die "Unable to chdir to $outputDir: $!";
    opendir D, "." or die "Urgh: $!";
    print "searching for libhyphen in output directory\n";
# Name examples:
# Linux libhyphen.so.0.2.1
# Mac libhyphen.0.dylib
    my ($output) = grep {
      unless (!readlink($_) and /^libhyphen(\.\d+)*\.(so|dylib$)(\.\d+)*/) {
        print "  $_ ... skipping\n"; 0;
      }
      else {
        print "  $_ ... match\n"; 1;
      }
    } readdir D;
    closedir D;
    
    die "Unable to find dynamic hyphen lib in $outputDir" unless $output;
    
    cp("$outputDir/$output", $libName) 
		or die "Unable to copy $outputDir/$output to $Bin/native-lib/$libName: $!";
    
    system("rm -rf $ns");
}
elsif ($sysname eq 'Windows NT') { # Never tried this, any volunteers? -- bpeter
  
  if ($machine eq 'x86') {
    my $ns = untar;
    my $apiDir = "$ns/src/win_api";
    chdir $apiDir  or die "Unable to chdir to $apiDir";
    my $cmd = "devenv Hyphen.sln /build Release_dll /project libhyphen";
    print "Compiling...\n";
    system($cmd) and die "Unable to run '$cmd' in $apiDir, good luck figuring out why";
    print "Done compiling.\n";
    
    cp "$apiDir/Release_dll/libhyphen/libhyphen.dll", $libName
    	or die "Unable to copy $apiDir/Release_dll/libhyphen/libhyphen.dll to $libName: $!";
    system("rm -rf $ns");
  } else {
    die "The architecture $machine is not supported, please fix native-build.pl";				
  }
} else {
	die "The $sysname on $machine is not supported, please fix native-build.pl";
}

print "Check out $libName\n";
exit 0;
