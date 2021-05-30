#ifndef GAME_MASTER_ASSERT_HPP
#define GAME_MASTER_ASSERT_HPP

#include <cstdlib>
#include <iostream>

#define ASSERT(e) ((void)((e) ? ((void)0) : ((void)(std::cerr << "Assertion failed: (" << #e << "), function " << __func__ << ", file " << __FILE__ << ", line " << __LINE__ << ".\n"), std::abort())))

#endif /* GAME_MASTER_ASSERT_HPP */
